package com.aki.akiwarmup.facebook.screen

import com.aki.akiwarmup.core.dsl.SceneExecutionContext
import com.aki.akiwarmup.core.dsl.ScreenBuilder
import com.aki.akiwarmup.core.dsl.all
import com.aki.akiwarmup.core.dsl.defineScreen
import com.aki.akiwarmup.core.dsl.desc
import com.aki.akiwarmup.core.dsl.descContains
import com.aki.akiwarmup.core.dsl.id
import com.aki.akiwarmup.core.dsl.text

/**
 * Định nghĩa màn hình Chọn nhóm (Choose Group) trên Facebook.
 * Màn hình được phát hiện khi giao diện xuất hiện văn bản "Chọn nhóm".
 *
 * @param context Ngữ cảnh thực thi cảnh hiện tại.
 * @param block Khối dựng cấu trúc màn hình [ScreenBuilder].
 */
fun onChooseGroupsView(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Choose Group", context) {
        detect { has(text("Chọn nhóm")) }
        apply(block)
    }

/**
 * Định nghĩa màn hình Trang chủ (Home View) trên Facebook.
 * Màn hình được phát hiện khi giao diện xuất hiện ô nhập liệu hoặc gợi ý "Bạn đang nghĩ gì?".
 *
 * @param context Ngữ cảnh thực thi cảnh hiện tại.
 * @param block Khối dựng cấu trúc màn hình [ScreenBuilder].
 */
fun onHomeView(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Home view", context) {
        detect { has(text("Bạn đang nghĩ gì?")) }
        apply(block)
    }

/**
 * Định nghĩa màn hình Lựa chọn nhóm để chia sẻ bài viết (Select Groups) trên Facebook.
 * Màn hình được phát hiện khi giao diện xuất hiện văn bản tiêu đề "Chia sẻ lên".
 *
 * @param context Ngữ cảnh thực thi cảnh hiện tại.
 * @param block Khối dựng cấu trúc màn hình [ScreenBuilder].
 */
fun onSelectGroupsView(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Select Group", context) {
        detect { has(text("Chia sẻ lên")) }
        apply(block)
    }

/**
 * Định nghĩa màn hình Soạn thảo nội dung đăng/Nhập caption (Input Caption) trên Facebook.
 * Màn hình được phát hiện khi xuất hiện dòng chữ/nút hành động "Đăng" trên thanh tiêu đề hoặc giao diện.
 *
 * @param context Ngữ cảnh thực thi cảnh hiện tại.
 * @param block Khối dựng cấu trúc màn hình [ScreenBuilder].
 */
fun onInputCaptionView(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Input Caption", context) {
        detect { has(text("Đăng")) }
        apply(block)
    }

/**
 * Định nghĩa màn hình Chỉnh sửa Video/Reels (Video Editor View) trên Facebook.
 * Màn hình được phát hiện khi xuất hiện đồng thời các công cụ: "Âm thanh", "Chỉnh sửa", "Hiệu ứng", "Văn bản".
 *
 * @param context Ngữ cảnh thực thi cảnh hiện tại.
 * @param block Khối dựng cấu trúc màn hình [ScreenBuilder].
 */
fun onVideoEditorView(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Video Editor View", context) {
        detect {
            all(
                text("Âm thanh"),
                text("Chỉnh sửa"),
                text("Hiệu ứng"),
                text("Văn bản")
            )
        }
        apply(block)
    }

/**
 * Định nghĩa màn hình Tìm kiếm/Chọn Âm thanh (Music Search View) trên Facebook.
 * Màn hình được phát hiện khi xuất hiện thanh tìm kiếm chứa mô tả "Tìm kiếm".
 *
 * @param context Ngữ cảnh thực thi cảnh hiện tại.
 * @param block Khối dựng cấu trúc màn hình [ScreenBuilder].
 */
fun onMusicSearchView(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Music Search View", context) {
        detect { has(descContains("Tìm kiếm")) }
        apply(block)
    }

/**
 * Định nghĩa màn hình Chia sẻ Thước phim/Reels (Share Reel View) trên Facebook.
 * Màn hình được phát hiện khi xuất hiện nút "Chia sẻ ngay" và mục "Ai có thể xem nội dung này?".
 *
 * @param context Ngữ cảnh thực thi cảnh hiện tại.
 * @param block Khối dựng cấu trúc màn hình [ScreenBuilder].
 */
fun onShareReelView(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Share Reel View", context) {
        detect {
            all(
                desc("Chia sẻ ngay"),
                desc("Ai có thể xem nội dung này?")
            )
        }
        apply(block)
    }