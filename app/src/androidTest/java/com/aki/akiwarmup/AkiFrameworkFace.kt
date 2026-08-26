package com.aki.akiwarmup

import android.graphics.Point
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.Direction
import com.aki.akiwarmup.core.dsl.clazz
import com.aki.akiwarmup.core.dsl.desc
import com.aki.akiwarmup.core.dsl.descContains
import com.aki.akiwarmup.core.dsl.runScene
import com.aki.akiwarmup.core.dsl.text
import com.aki.akiwarmup.core.logger.AkiLog
import com.aki.akiwarmup.core.logger.LogTag
import com.aki.akiwarmup.facebook.GroupPost
import com.aki.akiwarmup.facebook.action.chooseGroup
import com.aki.akiwarmup.facebook.action.editVideoAndAddMusic
import com.aki.akiwarmup.facebook.action.selectGroup
import com.aki.akiwarmup.facebook.action.selectMusicTrack
import com.aki.akiwarmup.facebook.action.shareReelWithCaption
import com.aki.akiwarmup.facebook.action.typeCaption
import com.aki.akiwarmup.facebook.scene.FaceBaseBehaviors
import com.aki.akiwarmup.facebook.scene.faceSceneDefine
import com.aki.akiwarmup.facebook.screen.onChooseGroupsView
import com.aki.akiwarmup.facebook.screen.onHomeView
import com.aki.akiwarmup.facebook.screen.onInputCaptionView
import com.aki.akiwarmup.facebook.screen.onMusicSearchView
import com.aki.akiwarmup.facebook.screen.onSelectGroupsView
import com.aki.akiwarmup.facebook.screen.onShareReelView
import com.aki.akiwarmup.facebook.screen.onVideoEditorView
import com.aki.akiwarmup.tiktok.action.keyWorlds
import com.aki.akiwarmup.tiktok.action.onChooseVideo
import com.aki.akiwarmup.tiktok.action.onTiktokSharePostAction
import com.aki.akiwarmup.tiktok.action.selectRandomMusic
import com.aki.akiwarmup.tiktok.action.selectUser
import com.aki.akiwarmup.tiktok.action.tapDeleteAndRepost
import com.aki.akiwarmup.tiktok.action.tapDeleteInRepostPopup
import com.aki.akiwarmup.tiktok.action.tapDeleteVideo
import com.aki.akiwarmup.tiktok.action.tapToAddMusic
import com.aki.akiwarmup.tiktok.action.tapToUpload
import com.aki.akiwarmup.tiktok.action.typeCaption
import com.aki.akiwarmup.tiktok.action.typeSearchKeyword
import com.aki.akiwarmup.tiktok.action.watchVideo
import com.aki.akiwarmup.tiktok.model.AutoRate
import com.aki.akiwarmup.tiktok.scene.tiktokSceneDefine
import com.aki.akiwarmup.tiktok.scene.TiktokBaseBehaviors
import com.aki.akiwarmup.tiktok.scene.TiktokCommentBehaviors
import com.aki.akiwarmup.tiktok.scene.TiktokDeleteVideoBehaviors
import com.aki.akiwarmup.tiktok.screen.onAddInfoView
import com.aki.akiwarmup.tiktok.screen.onDeletePopup
import com.aki.akiwarmup.tiktok.screen.onHome
import com.aki.akiwarmup.tiktok.screen.onProfile
import com.aki.akiwarmup.tiktok.screen.onRepostPopup
import com.aki.akiwarmup.tiktok.screen.onSearch
import com.aki.akiwarmup.tiktok.screen.onSearchResult
import com.aki.akiwarmup.tiktok.screen.onSelectMusicSheet
import com.aki.akiwarmup.tiktok.screen.onTiktokSharePost
import com.aki.akiwarmup.tiktok.screen.onVideoPreview
import com.aki.akiwarmup.tiktok.screen.onVideoView
import com.google.gson.Gson
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Lớp chứa các kịch bản kiểm thử tích hợp giao diện người dùng E2E (End-to-End) dành riêng cho Facebook
 * (và có thể mở rộng cho TikTok) dựa trên Aki Framework DSL.
 *
 * Để khởi chạy các kịch bản kiểm thử trong lớp này thông qua ADB (Android Debug Bridge), sử dụng lệnh instrumentation:
 * ```bash
 * adb shell am instrument -w -r -e class com.aki.akiwarmup.AkiFrameworkFace com.aki.akiwarmup.test/androidx.test.runner.AndroidJUnitRunner
 * ```
 */
@RunWith(AndroidJUnit4::class)
class AkiFrameworkFace {

    /**
     * Kịch bản tự động đăng bài lên các nhóm Facebook (Auto Post Groups).
     *
     * Kịch bản này tự động hóa quy trình tìm kiếm, tích chọn các nhóm Facebook theo từ khóa chỉ định,
     * sau đó viết caption và đăng bài viết.
     *
     * **Các đối số truyền vào qua Instrumentation Arguments (`context.args`):**
     * - `keywords`: Chuỗi chứa các từ khóa tìm kiếm nhóm, cách nhau bởi ký tự gạch đứng `|` (Ví dụ: `nhom 1|nhom 2|mua ban`).
     * - `caption`: Nội dung đoạn văn bản (caption) của bài viết cần đăng.
     *
     * **Luồng kịch bản chi tiết:**
     * 1. Khởi tạo cảnh Face với tên "Group Post". Tách tham số `keywords` thành danh sách. Nếu danh sách rỗng, dừng kiểm thử.
     * 2. Xử lý màn hình Chọn nhóm (`onChooseGroupsView`): Quét và nhấn chọn các nhóm phù hợp từ danh sách bằng [chooseGroup].
     * 3. Xử lý màn hình Chia sẻ bài viết (`onSelectGroupsView`): Quét và tích chọn thêm các nhóm để chia sẻ bài viết bằng [selectGroup].
     * 4. Đọc tham số `caption` từ đối số. Nếu caption rỗng, dừng kiểm thử.
     * 5. Xử lý màn hình Nhập caption (`onInputCaptionView`): Soạn thảo văn bản và nhấn Đăng bài bằng [typeCaption].
     * 6. Xử lý màn hình Trang chủ (`onHomeView`): Đợi quá trình tải lên hoàn tất và dừng kịch bản với trạng thái thành công ("OK").
     */
    @Test
    fun autoPostGroups() = runScene {
        scene {
            faceSceneDefine("Group Post", context) {
                include(FaceBaseBehaviors)
                AkiLog.e(LogTag.ENGINE,(context.args.getString("data") ?: ""))
                val data = Gson().fromJson<GroupPost>((context.args.getString("data") ?: ""),
                    GroupPost::class.java)
                AkiLog.e(LogTag.ENGINE,data.toString())
                val keywords = data.keywords
                if (keywords.isEmpty()) {
                    context.stop("Danh sách từ khóa rỗng")
                }

                onChooseGroupsView(context) {
                    chooseGroup(context, data)
                }

                onSelectGroupsView(context) {
                    selectGroup(context, data)
                }

                val captions = data.captions
                if (captions.isEmpty()) {
                    context.stop("Caption rỗng")
                }
                onInputCaptionView(context) {
                    typeCaption(context, captions.random())
                }

                onHomeView(context) {
                    action("Wait for upload") {
                        stop("OK")
                    }
                }
            }
        }

        loop {  }
    }

    /**
     * Kịch bản tự động tạo và đăng Thước phim (Reels / Video Post) lên Facebook.
     *
     * Kịch bản này tự động hóa quy trình:
     * 1. Xử lý màn hình Chỉnh sửa Video ([onVideoEditorView]):
     *    - Thêm âm thanh nếu chưa có, hoặc đổi định dạng nhãn dán nhạc nếu đã được chọn ([editVideoAndAddMusic]).
     *    - Nhấn "Tiếp" để chuyển sang màn hình chia sẻ.
     * 2. Xử lý màn hình Tìm kiếm/Chọn Âm thanh ([onMusicSearchView]):
     *    - Cuộn danh sách và chọn ngẫu nhiên bài hát phù hợp ([selectMusicTrack]).
     * 3. Xử lý màn hình Chia sẻ Thước phim ([onShareReelView]):
     *    - Soạn thảo nội dung bài đăng (caption) và nhấn nút "Chia sẻ ngay" ([shareReelWithCaption]).
     *
     * **Các đối số truyền vào qua Instrumentation Arguments (`context.args`):**
     * - `caption`: Nội dung đoạn văn bản (caption) của Reel cần đăng (tùy chọn).
     */
    @Test
    fun autoPostReels() = runScene {
        scene {
            faceSceneDefine("Reel Post", context) {
                include(FaceBaseBehaviors)

                onVideoEditorView(context) {
                    editVideoAndAddMusic(context)
                }

                onMusicSearchView(context) {
                    selectMusicTrack(context)
                }

                onShareReelView(context) {
                    val caption = context.args.getString("caption", "")
                    shareReelWithCaption(context, caption)
                }
            }
        }

        loop { }
    }

    /**
     * Alias giữ tương thích ngược cho kịch bản [autoPostReels].
     */
    @Test
    fun facebook() = autoPostReels()
}
