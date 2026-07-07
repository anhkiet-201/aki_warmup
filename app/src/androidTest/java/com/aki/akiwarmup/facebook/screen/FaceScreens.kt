package com.aki.akiwarmup.facebook.screen

import com.aki.akiwarmup.core.dsl.SceneExecutionContext
import com.aki.akiwarmup.core.dsl.ScreenBuilder
import com.aki.akiwarmup.core.dsl.defineScreen
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