package com.aki.akiwarmup

import android.graphics.Point
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.Direction
import com.aki.akiwarmup.core.dsl.clazz
import com.aki.akiwarmup.core.dsl.runScene
import com.aki.akiwarmup.core.dsl.text
import com.aki.akiwarmup.core.logger.AkiLog
import com.aki.akiwarmup.core.logger.LogTag
import com.aki.akiwarmup.tiktok.action.keyWorlds
import com.aki.akiwarmup.tiktok.action.onChooseVideo
import com.aki.akiwarmup.tiktok.action.onTiktokSharePostAction
import com.aki.akiwarmup.tiktok.action.selectRandomMusic
import com.aki.akiwarmup.tiktok.action.selectUser
import com.aki.akiwarmup.tiktok.action.selectVideoAfterSearch
import com.aki.akiwarmup.tiktok.action.tapAutoCut
import com.aki.akiwarmup.tiktok.action.tapDeleteAndRepost
import com.aki.akiwarmup.tiktok.action.tapDeleteInRepostPopup
import com.aki.akiwarmup.tiktok.action.tapDeleteVideo
import com.aki.akiwarmup.tiktok.action.tapText
import com.aki.akiwarmup.tiktok.action.tapToAddMusic
import com.aki.akiwarmup.tiktok.action.tapToUpload
import com.aki.akiwarmup.tiktok.action.typeCaption
import com.aki.akiwarmup.tiktok.action.typeSearchKeyword
import com.aki.akiwarmup.tiktok.action.watchVideo
import com.aki.akiwarmup.tiktok.model.AutoRate
import com.aki.akiwarmup.tiktok.scene.TiktokBaseBehaviors
import com.aki.akiwarmup.tiktok.scene.TiktokCommentBehaviors
import com.aki.akiwarmup.tiktok.scene.TiktokDeleteVideoBehaviors
import com.aki.akiwarmup.tiktok.scene.tiktokSceneDefine
import com.aki.akiwarmup.tiktok.screen.onAddInfoView
import com.aki.akiwarmup.tiktok.screen.onChooseTemplate
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
 * Lớp chứa các kịch bản kiểm thử tích hợp giao diện người dùng E2E cho ứng dụng TikTok 
 * dựa trên Aki Framework DSL.
 */
@RunWith(AndroidJUnit4::class)
class AkiFrameworkTest {

     /**
     * Kịch bản Khởi động (Warm-up) tài khoản TikTok.
     * 
     * Kịch bản này nhằm thực hiện các tương tác tự nhiên ban đầu (lướt xem, tìm kiếm ngẫu nhiên) để tạo độ tin cậy (trust) cho tài khoản.
     * 
     * Luồng kịch bản chi tiết:
     * 1. Khởi chạy ứng dụng TikTok (`launchApp`).
     * 2. Xử lý màn hình Trang chủ (`onHome`):
     *    - Thực hiện hành động lướt xem video (`watchVideo`) với các tương tác ngẫu nhiên.
     *    - Khi hoàn thành xem video, tìm và nhấn vào biểu tượng Tìm kiếm trên thanh tiêu đề để chuyển sang màn hình tìm kiếm.
     * 3. Xử lý màn hình Tìm kiếm (`onSearch`):
     *    - Chọn ngẫu nhiên một từ khóa từ danh sách từ khóa mồi [keyWorlds] và thực hiện gõ tìm kiếm (`typeSearchKeyword`).
     * 4. Xử lý màn hình Kết quả tìm kiếm (`onSearchResult`):
     *    - Nhấn chọn tab "Video" và tap ngẫu nhiên vào 1 trong các video đầu tiên để mở trình phát video (`selectVideoAfterSearch`).
     * 5. Xử lý màn hình Trình phát video (`onVideoView`):
     *    - Tiếp tục chạy hành động xem video (`watchVideo`).
     *    - Khi hoàn tất kịch bản xem video, nhấn nút Home (`pressHome`), buộc dừng ứng dụng (`killApp`) và kết thúc kịch bản (`stop("Hoàn thành")`).
     * 6. Xử lý các màn hình cảnh báo không xác định (`onUnknowView`):
     *    - Tự động đóng/bỏ qua các dialog thông báo (`onUnknowViewAction`).
     * 7. Cơ chế phục hồi lỗi (`handleUnknowScreen`):
     *    - Nếu gặp liên tiếp quá 3 lần màn hình không xác định, tiến hành dừng kịch bản kiểm thử với lý do lỗi ứng dụng.
     */
    @Test
    fun warmUp() = runScene {
        scene {
            tiktokSceneDefine("WarmUp", context) {
                include(TiktokBaseBehaviors)
                include(TiktokCommentBehaviors)

                handleUnknowScreen {
                    AkiLog.w(LogTag.ENGINE, "restart #${context.restartCount}")
                    if (this.context.restartCount > 3) {
                        this.context.stop("lỖI APP")
                    }
                }

                launchApp()
                onHome(context) {
                    watchVideo(context) {
                        find("com.ss.android.ugc.trill:id/jb1")?.let {
                            tap(it)
                            wait(300)
                        }
                    }
                }

                onSearch(context) {
                    typeSearchKeyword(context, keyWorlds.random())
                }

                onSearchResult(context) {
                    selectVideoAfterSearch(context)
                }

                onVideoView(context) {
                    watchVideo(context) {
                        pressHome()
                        this@tiktokSceneDefine.killApp()
                        stop("Hoàn thành")
                    }
                }
            }
        }

        loop {

        }
    }

    /**
     * Kịch bản Đăng bài tự động (Auto Post) từ luồng chia sẻ ngoài ứng dụng.
     * 
     * Kịch bản này được kích hoạt khi hệ thống chia sẻ một tệp video từ bên ngoài (hoặc thư viện) vào TikTok.
     * 
     * Luồng kịch bản chi tiết:
     * 1. Xử lý màn hình Chia sẻ bài viết (`onTiktokSharePost`):
     *    - Chuyển tiếp sang màn hình chỉnh sửa video bằng cách nhấn chọn tab Video (`onTiktokSharePostAction`).
     * 2. Xử lý màn hình Xem trước video (`onVideoPreview`):
     *    - Thực hiện nhấn nút Thêm âm thanh (`tapToAddMusic`) để chuyển sang sheet chọn nhạc nền.
     * 3. Xử lý sheet Chọn nhạc (`onSelectMusicSheet`):
     *    - Lựa chọn ngẫu nhiên một bài hát từ danh sách đề xuất (`selectRandomMusic`) để chèn vào video, sau đó quay lại.
     * 4. Xử lý màn hình Soạn thảo thông tin (`onAddInfoView`):
     *    - Thực hiện nhập văn bản caption lấy từ đối số kịch bản (`typeCaption`) và bấm nút Đăng, đợi quá trình upload hoàn tất rồi dừng.
     * 5. Cơ chế phục hồi lỗi (`handleUnknowScreen`):
     *    - Nếu gặp liên tiếp quá 8 lần màn hình không xác định hoặc lỗi không nhận dạng được giao diện, dừng kịch bản kiểm thử với mã lỗi thất bại.
     */
    @Test
    fun autoPost() = runScene {
        scene {
            tiktokSceneDefine("Auto Post", context) {
                handleUnknowScreen {
                    if (this.context.consecutiveUnknownScreens > 8) {
                        context.stop("Failure", -2)
                    }
                }
                onTiktokSharePost(context) {
                    onTiktokSharePostAction(context)
                }

                onVideoPreview(context) {
                    tapToAddMusic(context)
                }

                onSelectMusicSheet(context) {
                    selectRandomMusic(context)
                }

                onAddInfoView(context) {
                    typeCaption(context)
                }
            }
        }

        loop {

        }
    }

    /**
     * Kịch bản Đăng bài tự động (Auto Post) kết hợp tính năng Tự động cắt (Auto Cut) và thêm văn bản.
     *
     * Kịch bản này chia sẻ video vào TikTok, áp dụng tính năng Auto Cut để chọn mẫu dựng video,
     * tự động chèn thêm văn bản tùy chỉnh vào video, sau đó tiến hành đăng bài viết.
     *
     * **Các đối số truyền vào qua `context.args`:**
     * - `text`: Chuỗi văn bản cần chèn đè lên video (mặc định là "Xin chào"). Nếu truyền chuỗi rỗng, bỏ qua bước chèn văn bản.
     *
     * **Luồng kịch bản chi tiết:**
     * 1. Xử lý màn hình Chia sẻ bài viết (`onTiktokSharePost`): Nhấn chọn tab Video (`onTiktokSharePostAction`).
     * 2. Xử lý màn hình Xem trước video (`onVideoPreview`):
     *    - Nếu chưa nhấn Auto Cut, thực hiện gọi [tapAutoCut].
     *    - Nếu chưa chèn văn bản và đối số `text` không rỗng, thực hiện gọi [tapText] để chèn văn bản.
     *    - Sau khi hoàn thành hai bước trên, thực hiện kéo thả ô nhập văn bản (EditText) đến vị trí ngẫu nhiên trên màn hình và nhấn nút "Tiếp".
     * 3. Xử lý màn hình Chọn mẫu (`onChooseTemplate`): Cuộn ngẫu nhiên qua các mẫu template và nhấn chọn ngẫu nhiên một mẫu phù hợp.
     * 4. Xử lý màn hình Soạn thảo thông tin (`onAddInfoView`): Nhập caption và nhấn Đăng bài (`typeCaption`).
     */
    @Test
    fun autoPostWithAutoCut() = runScene {
        var hasTapAutoCut = false
        var hasTapText = false
        val text = context.args.getString("text") ?: "Xin chào"
        scene {
            tiktokSceneDefine("Auto Post", context) {
                handleUnknowScreen {
                    if (this.context.consecutiveUnknownScreens > 8) {
                        context.stop("Failure", -2)
                    }
                }
                onTiktokSharePost(context) {
                    onTiktokSharePostAction(context)
                }

                onVideoPreview(context) {
                    if (!hasTapAutoCut) {
                        tapAutoCut(context) {
                            hasTapAutoCut = true
                        }
                    }
                    if (!hasTapText && text.isNotEmpty()) {
                        tapText(context, text) {
                            hasTapText = true
                        }
                    }
                    if (hasTapAutoCut && (hasTapText || text.isEmpty())) {
                        action("Tap 'Tiếp'") {
                            on(clazz("android.widget.EditText")) { editText ->
                                editText?.let {
                                    it.drag(
                                    Point(
                                        it.visibleBounds.centerX() + random(100) - 50,
                                        325 + random(100),
                                    ), random(180, 300))
                                }
                                wait(random(1500,2000))
                            }
                            on(text("Tiếp")) {
                                tap(it)
                                wait(random(1500,2000))
                            }
                        }
                    }
                }

                onChooseTemplate(context) {
                    action("Chọn mẫu") {
                        on(clazz("androidx.recyclerview.widget.RecyclerView")) {
                            while (true) {
                                if (random(100) < 60) {
                                    it?.scroll(Direction.RIGHT, 0.6f)
                                } else {
                                    val templates = it?.children
                                    tap(templates?.random())
                                    wait(random(3000,5000))
                                    pressBack()
                                    wait(random(3000,5000))
                                    endAction()
                                }
                            }
                        }
                    }
                }

                onAddInfoView(context) {
                    typeCaption(context)
                }
            }
        }

        loop {

        }
    }

    /**
     * Kịch bản Seeding (Tương tác mồi) tài khoản dựa trên từ khóa chỉ định.
     * 
     * Kịch bản này dùng để tìm kiếm một từ khóa/username cụ thể, đi vào trang cá nhân của họ và thực hiện tương tác (like, comment, favorite) 
     * trên các video của họ dựa trên tỷ lệ xác suất tùy biến.
     * 
     * Luồng kịch bản chi tiết:
     * 1. Kiểm tra đối số đầu vào: Lấy từ khóa `keyword` từ đối số ngữ cảnh (`context.args.getString("keyword")`). Nếu không có, dừng kịch bản.
     * 2. Khởi chạy ứng dụng TikTok (`launchApp`).
     * 3. Xử lý màn hình Trang chủ (`onHome`):
     *    - Lướt xem video ngẫu nhiên theo tỷ lệ seeding cấu hình sẵn (`watchVideo`).
     *    - Khi hoàn tất, bấm vào biểu tượng Tìm kiếm để chuyển sang trang tìm kiếm.
     * 4. Xử lý màn hình Tìm kiếm (`onSearch`):
     *    - Nhập từ khóa `keyword` và bấm tìm kiếm (`typeSearchKeyword`).
     * 5. Xử lý màn hình Kết quả tìm kiếm (`onSearchResult`):
     *    - Bấm chuyển qua tab Người dùng, tìm kiếm user trùng khớp với `keyword` (`selectUser`). Nếu không tìm thấy, bấm Home và dừng kịch bản.
     * 6. Xử lý màn hình Trang cá nhân (`onProfile`):
     *    - Tìm lưới danh sách video, chọn video đầu tiên (`onChooseVideo`) và tap vào để bắt đầu phát video.
     * 7. Xử lý màn hình Trình phát video (`onVideoView`):
     *    - Chạy hành động xem video (`watchVideo`) kèm theo tỷ lệ tương tác seeding.
     *    - Khi kết thúc, nhấn Home và kết thúc kịch bản kiểm thử.
     * 8. Xử lý các màn hình cảnh báo không xác định (`onUnknowView`):
     *    - Tự động đóng các pop-up/dialog hướng dẫn (`onUnknowViewAction`).
     * 9. Cơ chế phục hồi lỗi (`handleUnknowScreen`):
     *    - Nếu gặp liên tiếp quá 8 màn hình lỗi không xác định, nhấn Home và kết thúc kịch bản với lý do lỗi ứng dụng.
     */
    @Test
    fun seeding() = runScene {
        val rate = AutoRate()

        scene {
            tiktokSceneDefine("Seeding", context) {
                include(TiktokBaseBehaviors)
                include(TiktokCommentBehaviors)
                
                val rawKeyword = context.args.getString("keyword")?.split("|")
                if (rawKeyword == null) {
                    context.stop("Wrong Keyword")
                }
                val keyword = rawKeyword!!.first()
                val numOfVideos = rawKeyword.last().toIntOrNull() ?: 0

                launchApp()

                onHome(context) {
                    watchVideo(context, rate) {
                        find("com.ss.android.ugc.trill:id/jb1")?.let {
                            tap(it)
                            wait(300)
                        }
                    }
                }

                onSearch(context) {
                    typeSearchKeyword(context, keyword)
                }

                onProfile(context) {
                    onChooseVideo(context, {
                        stop("No Videos")
                    }) { videos ->
                        tap(videos[numOfVideos])
                    }
                }

                onSearchResult(context) {
                    selectUser(context, keyword) {
                        pressHome()
                        stop("Không tìm thấy User")
                    }
                }

                onVideoView(context) {
                    watchVideo(context, rate) {
                        pressHome()
                        stop("Hoàn thành seeding: $keyword")
                    }
                }

            }
        }

        loop {

        }
    }

    /**
     * Kịch bản Đăng lại tự động (Repost) đối với các video có lượt xem thấp.
     * 
     * Kịch bản này truy cập trang cá nhân của tài khoản, tìm kiếm các video có lượt xem dưới 10 (thường là 0 View do bóp tương tác), 
     * thực hiện xóa video đó và đăng lại chính video đó để cải thiện lượt tiếp cận.
     * 
     * Luồng kịch bản chi tiết:
     * 1. Xử lý màn hình Trang cá nhân (`onProfile`):
     *    - Duyệt qua danh sách các video đã đăng.
     *    - Tìm video đầu tiên có lượt xem nhỏ hơn 10. Nếu tìm thấy, thực hiện tap vào video đó để mở.
     *    - Nếu duyệt hết danh sách mà không có video nào dưới 10 lượt xem, dừng kịch bản kiểm thử.
     * 2. Xử lý màn hình Trình phát video (`onVideoView`):
     *    - Bấm nút chia sẻ/xem thêm (`openVideoMenu`) để mở bảng tùy chọn.
     * 3. Xử lý màn hình Tùy chọn chia sẻ (`onShare`):
     *    - Cuộn ngang sang phải để tìm và bấm nút Xóa video (`swipeToChooseDelete`).
     * 4. Xử lý popup Repost (`onRepostPopup`):
     *    - Bấm nút xác nhận "Xóa và Đăng lại" (`tapDeleteAndRepost`).
     * 5. Xử lý popup xác nhận Xóa (`onDeletePopup`):
     *    - Xác nhận xóa video (`tapDeleteVideo`), lúc này TikTok sẽ tự động mở màn hình chỉnh sửa video đã đăng để đăng lại.
     * 6. Xử lý màn hình Xem trước video (`onVideoPreview`):
     *    - Thực hiện quy trình thêm âm thanh cho video mới đăng lại (`tapToAddMusic`).
     * 7. Xử lý màn hình Soạn thảo thông tin (`onAddInfoView`):
     *    - Tiến hành đăng video (`tapToUpload`), chờ video tải lên hoàn tất trong 20-40 giây rồi kết thúc kịch bản.
     * 8. Xử lý các màn hình cảnh báo không xác định (`onUnknowView`):
     *    - Tự động đóng các pop-up/dialog hướng dẫn (`onUnknowViewAction`).
     * 9. Cơ chế phục hồi lỗi (`handleUnknowScreen`):
     *    - Nếu gặp liên tiếp quá 8 màn hình lỗi không xác định, dừng kịch bản với mã lỗi thất bại.
     */
    @Test
    fun rePost() = runScene {
        scene {
            tiktokSceneDefine("Tiktok repost", context) {
                include(TiktokBaseBehaviors)
                include(TiktokDeleteVideoBehaviors)
                
                onProfile(context) {
                    onChooseVideo(context, {
                        stop("No Videos")
                    }) { videos ->
                        for ((i, videoText) in videos.withIndex()) {
                            if ((videoText.text.replace(".", "").trim().toIntOrNull() ?: Int.MAX_VALUE) < 10) {
                                tap(videoText)
                                wait(random(1000, 3000))
                                return@onChooseVideo
                            }
                            if (i >= videos.size - 1) {
                                stop("Không tìm thấy video 0 View nào")
                            }
                        }
                    }
                }

                onRepostPopup(context) {
                    tapDeleteAndRepost(context)
                }

                onDeletePopup(context) {
                    tapDeleteVideo(context) {
                        pressHome()
                        stop("Đã xóa video")
                    }
                }

                onVideoPreview(context) {
                    tapToAddMusic(context)
                }

                onAddInfoView(context) {
                    tapToUpload(context) {
                        wait(random(20000, 40000))
                        stop("Đã đăng lại video")
                    }
                }

            }
        }

        loop {

        }
    }

    /**
     * Kịch bản Xóa tự động các video có lượt xem thấp (Delete Zero View).
     * 
     * Kịch bản này truy cập trang cá nhân của tài khoản, tìm kiếm các video có lượt xem dưới 10 lượt xem 
     * và thực hiện xóa hoàn toàn các video này để dọn dẹp kênh.
     * 
     * Luồng kịch bản chi tiết:
     * 1. Xử lý màn hình Trang cá nhân (`onProfile`):
     *    - Duyệt qua danh sách các video đã đăng.
     *    - Tìm video đầu tiên có lượt xem dưới 10. Nếu tìm thấy, thực hiện tap vào video đó để mở.
     *    - Nếu đã duyệt hết mà không còn video nào dưới 10 lượt xem, dừng kịch bản kiểm thử (báo cáo đã xóa xong hoặc không tìm thấy).
     * 2. Xử lý màn hình Trình phát video (`onVideoView`):
     *    - Bấm mở menu tùy chọn/chia sẻ của video (`openVideoMenu`).
     * 3. Xử lý màn hình Tùy chọn chia sẻ (`onShare`):
     *    - Cuộn ngang sang phải để tìm và bấm nút Xóa video (`swipeToChooseDelete`).
     * 4. Xử lý popup Repost (`onRepostPopup`):
     *    - Thực hiện bấm nút Xóa đơn thuần (chỉ xóa, không đăng lại) thông qua (`tapDeleteInRepostPopup`), 
     *      đánh dấu biến trạng thái `hasDeleteVideo = true`, chờ 2-5 giây rồi bấm Back quay lại trang cá nhân.
     * 5. Xử lý popup xác nhận Xóa (`onDeletePopup`):
     *    - Thực hiện xác nhận xóa video (`tapDeleteVideo`), đánh dấu biến trạng thái `hasDeleteVideo = true`, 
     *      chờ 2-5 giây rồi bấm Back quay lại trang cá nhân để tiếp tục tìm và xóa video tiếp theo.
     * 6. Xử lý các màn hình cảnh báo không xác định (`onUnknowView`):
     *    - Tự động đóng các pop-up/dialog hướng dẫn (`onUnknowViewAction`).
     * 7. Cơ chế phục hồi lỗi (`handleUnknowScreen`):
     *    - Nếu gặp liên tiếp quá 8 màn hình lỗi không xác định, dừng kịch bản với mã lỗi thất bại.
     */
    @Test
    fun delete0() = runScene {
        var hasDeleteVideo = false

        scene {
            tiktokSceneDefine("Delete zero view video", context) {
                include(TiktokBaseBehaviors)
                include(TiktokDeleteVideoBehaviors)
                
                onProfile(context) {
                    onChooseVideo(context, {
                        stop("No Videos")
                    }) { videos ->
                        // Fix: dùng withIndex() thay vì 0..(videos.size) để tránh IndexOutOfBounds
                        for ((i, videoText) in videos.withIndex()) {
                            if ((videoText.text.trim().toIntOrNull() ?: Int.MAX_VALUE) < 10) {
                                tap(videoText)
                                wait(random(1000, 3000))
                                return@onChooseVideo
                            }
                            if (i >= videos.size - 1) {
                                stop(if (hasDeleteVideo) "Đã xóa tất cả video 0 View" else "Không tìm thấy video 0 View nào")
                            }
                        }
                    }
                }

                onRepostPopup(context) {
                    tapDeleteInRepostPopup(context) {
                        hasDeleteVideo = true
                        wait(random(2000, 5000))
                        pressBack()
                    }
                }

                onDeletePopup(context) {
                    tapDeleteVideo(context) {
                        hasDeleteVideo = true
                        wait(random(2000, 5000))
                        pressBack()
                    }
                }

            }
        }

        loop {

        }
    }
}
