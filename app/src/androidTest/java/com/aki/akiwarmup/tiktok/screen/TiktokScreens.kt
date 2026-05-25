package com.aki.akiwarmup.tiktok.screen

import com.aki.akiwarmup.core.dsl.SceneExecutionContext
import com.aki.akiwarmup.core.dsl.ScreenBuilder
import com.aki.akiwarmup.core.dsl.defineScreen
import com.aki.akiwarmup.core.dsl.id
import com.aki.akiwarmup.core.dsl.text

/**
 * Định nghĩa màn hình Trang chủ (Home) của ứng dụng TikTok.
 *
 * Màn hình được phát hiện khi đồng thời thỏa mãn các điều kiện:
 * 1. Chứa nhãn văn bản "Trang chủ" (`TiktokText.HOME`).
 * 2. Chứa nút ảnh đại diện của người dùng hiện tại (`TiktokId.USER_AVATAR`).
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param block Khối Lambda thiết lập các hành động tương tác sẽ được áp dụng trên màn hình này.
 */
fun onHome(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Home", context) {
        detect {
            all(
                text(TiktokText.HOME), id(TiktokId.USER_AVATAR)
            )
        }
        apply(block)
    }

/**
 * Định nghĩa màn hình Tìm kiếm (Search) của ứng dụng TikTok.
 *
 * Màn hình được phát hiện khi đồng thời thỏa mãn các điều kiện:
 * 1. Chứa thanh nhập từ khóa tìm kiếm (`TiktokId.SEARCH_BAR`).
 * 2. Chứa nhãn hoặc gợi ý văn bản "Tìm kiếm" (`TiktokText.SEARCH`).
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param block Khối Lambda thiết lập các hành động tương tác sẽ được áp dụng trên màn hình này.
 */
fun onSearch(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Search", context) {
        detect {
            has(id(TiktokId.SEARCH_BAR) and text(TiktokText.SEARCH))
        }
        apply(block)
    }

/**
 * Định nghĩa màn hình Kết quả tìm kiếm (Search Result) của ứng dụng TikTok.
 *
 * Màn hình được phát hiện bằng cách kiểm tra sự hiện diện của ViewPager kết quả tìm kiếm (`TiktokId.SEARCH_PAGER`), 
 * nơi chứa các tab phân loại kết quả như "Top", "Video", "Người dùng", v.v.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param block Khối Lambda thiết lập các hành động tương tác sẽ được áp dụng trên màn hình này.
 */
fun onSearchResult(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Search Result", context) {
        detect {
            has(id(TiktokId.SEARCH_PAGER))
        }
        apply(block)
    }

/**
 * Định nghĩa màn hình trình phát video (Video View) của TikTok (màn hình cuộn xem video).
 *
 * Màn hình được phát hiện khi đồng thời thỏa mãn các điều kiện:
 * 1. Chứa nút Tìm kiếm (`TiktokText.SEARCH`) ở góc trên màn hình.
 * 2. Chứa ảnh đại diện của tài khoản đăng tải video (`TiktokId.USER_AVATAR`).
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param block Khối Lambda thiết lập các hành động tương tác sẽ được áp dụng trên màn hình này.
 */
fun onVideoView(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Video View", context) {
        detect { has(text(TiktokText.SEARCH) and id(TiktokId.USER_AVATAR)) }
        apply(block)
    }

/**
 * Định nghĩa các màn hình cảnh báo, hướng dẫn hoặc pop-up hệ thống xuất hiện bất ngờ không mong muốn (Unknown View).
 *
 * Màn hình được phát hiện khi xuất hiện ít nhất một trong các nút:
 * - Nút "Đã hiểu" (thường dùng để xác nhận thông báo/hướng dẫn).
 * - Nút "Không cho phép" (thường dùng để từ chối các quyền truy cập ứng dụng yêu cầu).
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param block Khối Lambda thiết lập các hành động tương tác (như tự động dismiss) trên màn hình này.
 */
fun onUnknowView(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Unknown", context) {
        detect { any(text("Đã hiểu"), text("Không cho phép")) }
        apply(block)
    }

/**
 * Định nghĩa màn hình Chia sẻ bài viết lên TikTok xuất hiện khi chia sẻ video từ các ứng dụng ngoài hoặc thư viện hệ thống.
 *
 * Màn hình được phát hiện khi xuất hiện nhãn tiêu đề "Chia sẻ lên TikTok".
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param block Khối Lambda thiết lập các hành động tương tác sẽ được áp dụng trên màn hình này.
 */
fun onTiktokSharePost(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Tiktok Share Post", context) {
        detect { has(text("Chia sẻ lên TikTok")) }
        apply(block)
    }

/**
 * Định nghĩa màn hình Xem trước video (Video Preview) ngay trước khi thực hiện viết caption/đăng bài.
 *
 * Màn hình được phát hiện bằng cách tìm sự hiện diện của nút/văn bản Thêm âm thanh (`TiktokId.ADD_SOUND_TEXT`).
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param block Khối Lambda thiết lập các hành động tương tác sẽ được áp dụng trên màn hình này.
 */
fun onVideoPreview(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Video Preview", context) {
        detect { has(id("com.ss.android.ugc.trill:id/zo6")) }
        apply(block)
    }

/**
 * Định nghĩa sheet chọn nhạc nền (Select Music Sheet) trong quy trình tải lên video.
 *
 * Màn hình được phát hiện bằng cách tìm sự hiện diện của danh sách đề xuất âm thanh (`TiktokId.MUSIC_LIST`).
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param block Khối Lambda thiết lập các hành động tương tác sẽ được áp dụng trên màn hình này.
 */
fun onSelectMusicSheet(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Select Music", context) {
        detect { has(id("com.ss.android.ugc.trill:id/t96")) }
        apply(block)
    }

/**
 * Định nghĩa màn hình Thêm thông tin bài đăng (Add Info View) như viết caption, chèn hashtag trước khi bấm nút Đăng.
 *
 * Màn hình được phát hiện bằng cách tìm sự hiện diện của ô nhập caption (`TiktokId.CAPTION_INPUT`).
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param block Khối Lambda thiết lập các hành động tương tác sẽ được áp dụng trên màn hình này.
 */
fun onAddInfoView(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Add Info", context) {
        detect { has(id("com.ss.android.ugc.trill:id/gfw")) }
        apply(block)
    }

/**
 * Định nghĩa màn hình Trang cá nhân (Profile) của người dùng hiện tại hoặc của người dùng khác.
 *
 * Màn hình được phát hiện bằng cách kiểm tra sự hiện diện của lưới danh sách các video đã tải lên (`TiktokId.PROFILE_VIDEO_GRID`).
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param block Khối Lambda thiết lập các hành động tương tác sẽ được áp dụng trên màn hình này.
 */
fun onProfile(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Profile", context) {
        detect { has(id("com.ss.android.ugc.trill:id/hdm")) }
        apply(block)
    }

/**
 * Định nghĩa bảng tùy chọn chia sẻ hoặc xem thêm (Share Sheet) của trình phát video.
 *
 * Màn hình được phát hiện bằng cách tìm sự hiện diện của thanh danh sách tùy chọn/chia sẻ video (`TiktokId.SHARE_OPTIONS_LIST`).
 * Note: Ở đây sử dụng resource ID tương ứng với danh sách option chia sẻ.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param block Khối Lambda thiết lập các hành động tương tác sẽ được áp dụng trên màn hình này.
 */
fun onShare(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Share", context) {
        detect { has(id("com.ss.android.ugc.trill:id/znd")) }
        apply(block)
    }

/**
 * Định nghĩa hộp thoại thông báo xác nhận Đăng lại (Repost Popup).
 *
 * Hộp thoại được phát hiện bằng cách tìm sự hiện diện của view với ID là `com.ss.android.ugc.trill:id/ofw`.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param block Khối Lambda thiết lập các hành động tương tác sẽ được áp dụng trên màn hình này.
 */
fun onRepostPopup(
    context: SceneExecutionContext, block: ScreenBuilder.() -> Unit
) = defineScreen("Repost Popup", context) {
    detect { has(id("com.ss.android.ugc.trill:id/ofw")) }
    apply(block)
}

/**
 * Định nghĩa hộp thoại thông báo xác nhận Xóa video (Delete Popup).
 *
 * Hộp thoại được phát hiện bằng cách tìm sự hiện diện của view xác nhận với ID là `com.ss.android.ugc.trill:id/xd`.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param block Khối Lambda thiết lập các hành động tương tác sẽ được áp dụng trên màn hình này.
 */
fun onDeletePopup(
    context: SceneExecutionContext, block: ScreenBuilder.() -> Unit
) = defineScreen("Delete Popup", context) {
    detect { has(id("com.ss.android.ugc.trill:id/xd")) }
    apply(block)
}
