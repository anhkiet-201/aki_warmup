package com.aki.akiwarmup.facebook.screen

import com.aki.akiwarmup.core.dsl.SceneExecutionContext
import com.aki.akiwarmup.core.dsl.ScreenBuilder
import com.aki.akiwarmup.core.dsl.defineScreen
import com.aki.akiwarmup.core.dsl.id
import com.aki.akiwarmup.core.dsl.text

/**
 * Định nghĩa màn hình Chọn nhóm (Choose Group) trên Facebook.
 * Màn hình được phát hiện khi giao diện xuất hiện dòng chữ "Chọn nhóm".
 *
 * @param context Ngữ cảnh thực thi cảnh hiện tại.
 * @param block Khối dựng cấu trúc màn hình ScreenBuilder.
 */
fun onChooseGroupsView(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Choose Group", context) {
        detect { has(text("Chọn nhóm")) }
        apply(block)
    }

/**
 * Định nghĩa màn hình Soạn thảo/Nhập caption (Input Caption) trên Facebook.
 * Màn hình được phát hiện khi xuất hiện dòng chữ/nút "Đăng" trên thanh tiêu đề/giao diện đăng.
 *
 * @param context Ngữ cảnh thực thi cảnh hiện tại.
 * @param block Khối dựng cấu trúc màn hình ScreenBuilder.
 */
fun onInputCaptionView(context: SceneExecutionContext, block: ScreenBuilder.() -> Unit) =
    defineScreen("Input Caption", context) {
        detect { has(text("Đăng")) }
        apply(block)
    }