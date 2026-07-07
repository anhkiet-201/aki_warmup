package com.aki.akiwarmup.facebook.action

import androidx.test.uiautomator.UiObject2
import com.aki.akiwarmup.core.dsl.SceneExecutionContext
import com.aki.akiwarmup.core.dsl.clazz
import com.aki.akiwarmup.core.dsl.defineAction
import com.aki.akiwarmup.core.dsl.text
import com.aki.akiwarmup.core.logger.AkiLog
import com.aki.akiwarmup.core.logger.LogTag

/**
 * Bộ nhớ đệm (cache) lưu trữ danh sách tên các nhóm Facebook đã được chọn hoặc đã đăng bài.
 * Mục đích nhằm tránh việc nhấn chọn lặp lại cùng một nhóm trong các chu kỳ quét tiếp theo.
 */
val cacheSelectedGroups: MutableList<String> = mutableListOf()

/**
 * Lưu trữ tên của nhóm cuối cùng được quét thấy trong danh sách ở trang trước.
 * Dùng để đối chiếu và phát hiện xem danh sách nhóm đã được cuộn tới cuối hay chưa (khi không thay đổi tên nhóm cuối cùng sau khi cuộn).
 */
var lastedGroup = ""

/**
 * Hành động quét, tìm kiếm và nhấn chọn nhóm Facebook phù hợp từ màn hình danh sách nhóm.
 *
 * Hành động này duyệt qua RecyclerView chứa danh sách các nhóm, so khớp tên nhóm với danh sách từ khóa [keywords].
 * Nếu khớp từ khóa và nhóm chưa từng được xử lý (không nằm trong [cacheSelectedGroups]), hành động tiến hành tap vào nhóm đó.
 * Nếu không có nhóm nào phù hợp được hiển thị, hành động sẽ tự động thực hiện cuộn lên (swipeUp) để tiếp tục quét.
 * Khi cuộn đến cuối danh sách (phát hiện qua [lastedGroup] không đổi), hành động sẽ dừng kịch bản.
 *
 * @param context Ngữ cảnh thực thi cảnh hiện tại.
 * @param keywords Danh sách các từ khóa dùng để so khớp (không phân biệt hoa thường) với tên nhóm.
 */
fun chooseGroup(
    context: SceneExecutionContext,
    keywords: List<String>,
) = defineAction("Choose group", context) {
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
 * Hành động quét và tích chọn nhiều nhóm Facebook phù hợp để chuẩn bị chia sẻ bài viết.
 *
 * Hành động này tìm kiếm RecyclerView chính hiển thị danh sách nhóm (chiều cao > 1000 pixel),
 * duyệt qua các phần tử con để lấy ra tên nhóm. Nếu tên nhóm khớp với bất kỳ từ khóa nào trong [keywords]
 * và chưa được chọn trước đó, hành động sẽ nhấn vào nhóm đó để tích chọn.
 *
 * Trong quá trình chọn nhóm, nếu hệ thống hiển thị thông báo giới hạn chia sẻ ("Bạn đã đạt giới hạn chia sẻ."),
 * hành động sẽ tự động phát hiện và nhấn nút "Tiếp" để hoàn tất bước chọn nhóm.
 * Nếu cuộn đến cuối danh sách (phát hiện qua [lastedGroup] không đổi), hành động sẽ kiểm tra xem nút "Tiếp"
 * có được bật hay không; nếu có thì nhấn chọn để tiếp tục, nếu không thì nhấn Home và dừng kịch bản.
 *
 * @param context Ngữ cảnh thực thi cảnh hiện tại.
 * @param keywords Danh sách các từ khóa (không phân biệt chữ hoa/thường) dùng để lọc các nhóm muốn chia sẻ.
 */
fun selectGroup(
    context: SceneExecutionContext,
    keywords: List<String>,
) = defineAction("Select groups", context) {
    val view = findAll(clazz("androidx.recyclerview.widget.RecyclerView")).firstOrNull {
        it.visibleBounds.height() > 1000
    }
    view.let { recyclerView ->
        val groupElement = recyclerView?.children?.mapNotNull {
            it.children.getOrNull(1)?.children?.firstOrNull()
        } ?: emptyList<UiObject2>()
        groupElement.forEach {
            val text= it.text ?: ""
            if (keywords.any { k -> text.contains(k, ignoreCase = true) } && !cacheSelectedGroups.contains(text)) {
                tap(it)
                cacheSelectedGroups.add(text)
                on(text("Bạn đã đạt giới hạn chia sẻ.")) { limitText ->
                    if (limitText != null) {
                        on(text("Tiếp")) { nextButton ->
                            tap(nextButton)
                            wait(random(900, 1800))
                            endAction()
                        }
                    }
                }
                wait(random(900, 1800))
            }
        }
        swipeUp()
        val lastedText = groupElement.lastOrNull()?.text
        if (lastedText != null) {
            if (lastedGroup == lastedText) {
                on(text("Tiếp")) {
                    if (it?.isEnabled ?: false) {
                        tap(it)
                    } else {
                        pressHome()
                        stop(if (cacheSelectedGroups.isEmpty()) "Không tìm thấy nhóm khớp với từ khóa" else "Đã đăng ${cacheSelectedGroups.size} nhóm")
                    }
                }
            } else {
                lastedGroup = lastedText
            }
        }
        wait(random(1500, 3000))
    }
}

/**
 * Hành động soạn thảo nội dung bài đăng (caption) và bấm nút Đăng bài trên màn hình soạn bài viết.
 *
 * Hành động này định vị ô nhập liệu [android.widget.AutoCompleteTextView] để gán nội dung văn bản [caption],
 * sau đó nhấn vào nút chứa nhãn "Đăng" để tiến hành đăng bài viết lên Facebook và chờ từ 10 đến 15 giây để quá trình xử lý hoàn tất.
 *
 * @param context Ngữ cảnh thực thi cảnh hiện tại.
 * @param caption Nội dung chuỗi văn bản của bài viết cần đăng.
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