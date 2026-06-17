package com.aki.akiwarmup.facebook.action

import androidx.test.uiautomator.UiObject2
import com.aki.akiwarmup.core.dsl.SceneExecutionContext
import com.aki.akiwarmup.core.dsl.clazz
import com.aki.akiwarmup.core.dsl.defineAction
import com.aki.akiwarmup.core.dsl.text

/**
 * Bộ nhớ đệm lưu danh sách các nhóm đã được chọn/đăng bài để tránh lặp lại.
 */
val cacheSelectedGroups: MutableList<String> = mutableListOf()

/**
 * Tên nhóm cuối cùng được xử lý để phát hiện khi cuộn hết danh sách.
 */
var lastedGroup = ""

/**
 * Hành động tìm kiếm và chọn nhóm Facebook dựa trên danh sách từ khóa.
 *
 * Hành động này quét danh sách phần tử trong RecyclerView, so sánh tên nhóm với danh sách
 * từ khóa tìm kiếm. Nếu khớp và nhóm chưa được xử lý trước đó, nó sẽ nhấn vào nhóm đó.
 * Nếu không khớp, màn hình sẽ tự động cuộn lên để quét tiếp.
 *
 * @param context Ngữ cảnh thực thi cảnh hiện tại.
 * @param keywords Danh sách các từ khóa tìm kiếm nhóm.
 */
fun chooseGroup(
    context: SceneExecutionContext,
    keywords: List<String>,
) = defineAction("View Comment", context) {
    on(clazz("androidx.recyclerview.widget.RecyclerView")) { recyclerView ->
        val groupElement = recyclerView?.children?.mapNotNull {
            it.children.firstOrNull()?.children?.firstOrNull()
        } ?: emptyList<UiObject2>()
        groupElement.forEach {
            val text= it.text
            if (keywords.any { k -> text.contains(k, ignoreCase = true) } && !cacheSelectedGroups.contains(text)) {
                tap(it)
                cacheSelectedGroups.add(text)
                wait(random(1500, 3000))
                return@on
            }
        }
        swipeUp()
        val lastedText = groupElement.lastOrNull()?.text
        if (lastedText != null) {
            if (lastedGroup == lastedText) {
                pressHome()
                stop(if (cacheSelectedGroups.isEmpty()) "Không tìm thấy nhóm khớp với từ khóa" else "Đã đăng ${cacheSelectedGroups.size} nhóm")
            } else {
                lastedGroup = lastedText
            }
        }
        wait(random(1500, 3000))
    }
    endAction()
}

/**
 * Hành động soạn thảo nội dung bài đăng (caption) và bấm Đăng bài.
 *
 * Hành động này tìm kiếm ô nhập AutoCompleteTextView để thiết lập nội dung caption,
 * sau đó nhấn vào nút "Đăng" và đợi quá trình đăng hoàn tất.
 *
 * @param context Ngữ cảnh thực thi cảnh hiện tại.
 * @param caption Nội dung văn bản bài viết cần đăng.
 */
fun typeCaption(
    context: SceneExecutionContext,
    caption: String,
) = defineAction("View Comment", context) {
    on(clazz("android.widget.AutoCompleteTextView")) {
        it?.text = caption
        wait(random(1555, 3009))
    }
    on(text("Đăng")) {
        tap(it)
        wait(random(10000, 15000))
    }
}