package com.aki.akiwarmup

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aki.akiwarmup.core.dsl.runScene
import com.aki.akiwarmup.facebook.action.chooseGroup
import com.aki.akiwarmup.facebook.action.selectGroup
import com.aki.akiwarmup.facebook.action.typeCaption
import com.aki.akiwarmup.facebook.scene.FaceBaseBehaviors
import com.aki.akiwarmup.facebook.scene.faceSceneDefine
import com.aki.akiwarmup.facebook.screen.onChooseGroupsView
import com.aki.akiwarmup.facebook.screen.onHomeView
import com.aki.akiwarmup.facebook.screen.onInputCaptionView
import com.aki.akiwarmup.facebook.screen.onSelectGroupsView
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
                val keywords = (context.args.getString("keywords") ?: "").split("|")
                if (keywords.isEmpty()) {
                    context.stop("Danh sách từ khóa rỗng")
                }

                onChooseGroupsView(context) {
                    chooseGroup(context, keywords)
                }
                onSelectGroupsView(context) {
                    selectGroup(context, keywords)
                }

                val caption = context.args.getString("caption") ?: ""
                if (caption.isEmpty()) {
                    context.stop("Caption rỗng")
                }
                onInputCaptionView(context) {
                    typeCaption(context, caption)
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
}
