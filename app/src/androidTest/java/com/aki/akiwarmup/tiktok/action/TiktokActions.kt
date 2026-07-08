package com.aki.akiwarmup.tiktok.action

import com.aki.akiwarmup.core.logger.AkiLog
import com.aki.akiwarmup.core.logger.LogTag
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiObject2
import com.aki.akiwarmup.core.dsl.ActionBuilder
import com.aki.akiwarmup.core.dsl.SceneExecutionContext
import com.aki.akiwarmup.core.dsl.clazz
import com.aki.akiwarmup.core.dsl.defineAction
import com.aki.akiwarmup.core.dsl.desc
import com.aki.akiwarmup.core.dsl.id
import com.aki.akiwarmup.core.dsl.text
import com.aki.akiwarmup.random.generateComment
import com.aki.akiwarmup.tiktok.model.AutoRate
import com.aki.akiwarmup.tiktok.model.RateType
import com.aki.akiwarmup.tiktok.screen.TiktokDesc
import com.aki.akiwarmup.tiktok.screen.TiktokId
import com.aki.akiwarmup.tiktok.screen.TiktokText

typealias Action = suspend ActionBuilder.() -> Unit

val captionKeyword = listOf(
    "việc làm",
    "thời vụ",
    "tìm việc",
    "tuyển dụng",
    "kcn",
    "Mỹ phước",
    "Vsip",
    "ttnhr",
    "vieclambinhduong",
    "vieclam",
    "công ty",
    "công việc",
    "đồng an",
    "Nam Tân uyên",
    "kcn"
)

val keyWorlds = listOf(
    "ttnhr",
    "việc làm bình dương",
    "việc làm thời vụ bình dương",
    "tìm việc vsip 2a",
    "việc làm ở mỹ phước",
    "tìm việc ttnhr",
    "việc làm nam tân uyên",
    "việc làm kcn sóng thần 3",
    "việc làm ở kcn vsip 2a",
    "việc làm mỹ phước tuyển dụng",
    "việc làm mỹ phước ttnhr",
    "tìm việc làm bến cát",
    "việc làm kcn đồng an 2",
    "việc làm vĩnh tân bình dương",
    "tìm việc làm ở tân uyên",
    "việc làm thời vụ st 3",
    "tuyển dụng ttnhr",
    "việc làm bình dương ttnhr",
    "việc làm vsip",
    "việc làm vsip 3",
    "việc làm hội nghĩa",
    "việc làm hành chính",
    "việc làm mỹ phước 1",
    "việc làm mỹ phước 2",
    "việc làm mỹ phước 3",
    "việc làm mỹ phước 4",
    "làm công ty ở bình dương",
    "làm công ty ở vsip 3",
    "làm công ty ở nam tân uyên"
)

/**
 * Lướt xem video TikTok và tự động thực hiện các hành động tương tác (like, comment, favorite, repost, copy link) 
 * dựa trên tỉ lệ ngẫu nhiên được cấu hình bởi [AutoRate].
 *
 * Hàm thực hiện lặp vô hạn (loop) cho đến khi gặp hành động [RateType.EXIT], tại đó sẽ gọi callback [onExit].
 * Các bước chính bên trong vòng lặp:
 * 1. Đọc mô tả video (`TiktokId.VIDEO_DESC`). Nếu mô tả không chứa từ khóa nào trong [captionKeyword], 
 *    hàm sẽ kích hoạt cơ chế `swipeBias` và lướt qua video khác nhanh hơn.
 * 2. Tìm dòng chữ "Quảng bá đề xuất" (`TiktokText.RECOMMENDED_PROMOTION`). Nếu tìm thấy, thực hiện cuộn lên (swipeUp) 
 *    và kết thúc hành động để tránh tương tác với quảng cáo.
 * 3. Lựa chọn hành động tương tác dựa trên xác suất cấu hình trong [AutoRate]:
 *    - `RateType.SWIPE`: Lướt lên xem video tiếp theo.
 *    - `RateType.LIKE`: Bấm nút Thích nếu chưa được chọn.
 *    - `RateType.FAVORITE`: Bấm nút Yêu thích nếu chưa được chọn.
 *    - `RateType.COMMENT`: Nếu mô tả video chứa hashtag "#ttnhr", bấm nút bình luận, nhập nội dung bình luận 
 *      ngẫu nhiên (sử dụng [generateComment]), bấm gửi rồi quay lại.
 *    - `RateType.RE_POST`: Chia sẻ bài viết dưới dạng Đăng lại.
 *    - `RateType.COPY_LINK`: Sao chép liên kết của video hiện tại.
 *    - `RateType.EXIT`: Kết thúc hành động lướt xem video và gọi callback [onExit].
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`), chứa thông tin phiên chạy.
 * @param rate Đối tượng cấu hình xác suất/tần suất các hành động tự động (`AutoRate`), mặc định là một instance mới.
 * @param id Định danh hành động, mặc định là "Watch Video".
 * @param onExit Callback hành động dạng suspend được gọi khi quyết định thoát xem video.
 */
fun watchVideo(
    context: SceneExecutionContext,
    rate: AutoRate = AutoRate(),
    id: String = "Watch Video",
    onExit: Action
) = defineAction(id, context) {
    var loopCount = 1
    loop {
        var hasDoAction = false
        on(id(TiktokId.VIDEO_DESC)) { desc ->
            val descText = desc?.text
            val beTTNHR = descText?.contains("#ttnhr") ?: false || descText?.contains("#vieclamttn") ?: false
            descText?.let {
                AkiLog.d(LogTag.CONTENT, "desc: ${descText.take(80)}")
                if (captionKeyword.none { kw -> it.lowercase().contains(kw.lowercase()) }) {
                    AkiLog.d(LogTag.CONTENT, "keyword miss → swipeBias")
                    rate.swipeBias()
                } else {
                    AkiLog.d(LogTag.CONTENT, "keyword hit ✓")
                    wait(random(1990, 30000 / loopCount))
                }
            }
            on(text(TiktokText.RECOMMENDED_PROMOTION)) {
                if (it != null) {
                    swipeUp()
                    hasDoAction = true
                    endAction()
                }
            }
            choose(
                rate[RateType.SWIPE] to {
                    rate.reset()
                    loopCount = 1
                    swipeUp()
                    hasDoAction = true
                },
                rate[RateType.LIKE] to {
                    find(id(TiktokId.LIKE_BUTTON))?.let {
                        rate.consume(RateType.LIKE)
                        if (!it.isSelected) {
                            tap(it)
                            wait(random(min = 1000, max = 3000))
                        }
                        hasDoAction = true
                    }
                },
                rate[RateType.FAVORITE] to {
                    find(id(TiktokId.FAVORITE_BUTTON))?.let {
                        rate.consume(RateType.FAVORITE)
                        if (!it.isSelected) {
                            tap(it)
                            wait(random(min = 1000, max = 3000))
                        }
                        hasDoAction = true
                    }
                },
                rate[RateType.COMMENT] to {
                    rate.consume(RateType.COMMENT)
                    if (beTTNHR) {
                        find(id(TiktokId.COMMENT_BUTTON))?.let { commentButton ->
                            tap(commentButton)
                            wait(random(100, 1500))
                            hasDoAction = true
                            endAction()
                        }
                    }
                },
                rate[RateType.RE_POST] to {
                    find(id(TiktokId.SHARE_BUTTON))?.let {
                        rate.consume(RateType.RE_POST)
                        tap(it)
                        wait(random(1000, 1500))
                        find(text(TiktokText.REPOST)).let { repost ->
                            if (repost == null) {
                                pressBack()
                            } else {
                                tap(repost)
                                wait(random(1000, 1500))
                            }
                        }
                        hasDoAction = true
                    }
                },
                rate[RateType.COPY_LINK] to {
                    find(id(TiktokId.SHARE_BUTTON))?.let {
                        rate.consume(RateType.COPY_LINK)
                        tap(it)
                        wait(random(1000, 1500))
                        find(text(TiktokText.COPY_LINK))?.let { repost ->
                            tap(repost)
                            wait(random(1000, 1500))
                        }
                        hasDoAction = true
                    }
                },
                rate[RateType.EXIT] to {
                    this.onExit()
                    rate.reset()
                    endAction()
                }
            )
        }
        if (!hasDoAction) {
            endAction()
        }
        loopCount++
    }
}

/**
 * Xem các bình luận của video hiện tại và thực hiện tương tác ngẫu nhiên.
 *
 * Quy trình thực hiện:
 * 1. Kiểm tra nếu ô nhập liệu (`TiktokId.COMMENT_INPUT`) đã có sẵn text thì gửi bình luận đó (bấm nút gửi và quay lại).
 * 2. Lấy danh sách bình luận (`TiktokId.COMMENT_LIST`), tìm các nút trả lời (`TiktokId.REPPLY_COMMENT_BUTTON`).
 * 3. Nếu có nút trả lời, thực hiện cuộn ngẫu nhiên danh sách lên để xem các comment bên dưới.
 * 4. Quyết định tương tác theo tỷ lệ 50-50:
 *    - 50%: Bấm vào ô nhập bình luận để chuẩn bị viết.
 *    - 50%: Bấm chọn ngẫu nhiên một nút trả lời của bình luận có sẵn.
 * 5. Kết thúc hành động.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun viewComment(
    context: SceneExecutionContext
) = defineAction("View Comment", context) {
    on(id(TiktokId.COMMENT_INPUT)) { textField ->
        if (textField?.text?.trim()?.isNotEmpty() ?: false) {
            findAll(desc(TiktokDesc.POST_COMMENT_BUTTON)).let { postButtons ->
                postButtons.forEach { postButton ->
                    if (postButton.isClickable) {
                        tap(postButton)
                    }
                }
                wait(random(100, 1500))
                pressBack()
                endAction()
            }
        }
        on(id(TiktokId.COMMENT_LIST)) { comments ->
            comments?.findObjects(id(TiktokId.REPPLY_COMMENT_BUTTON).toBySelector()).let {
                if (it?.isNotEmpty() ?: false) {
                    sometimes(50) {
                        comments?.swipe(Direction.UP, 0.6f)
                        wait(random(100, 1500))
                    }
                }
                choose(
                    5 to {
                        tap(textField)
                        wait(random(500, 1000))
                    },
                    5 to {
                        tap(it?.random())
                    }
                )
                wait(random(930, 1620))
            }
        }
    }
    endAction()
}

/**
 * Thêm một bình luận mới vào video.
 *
 * Hàm sẽ tìm tất cả các ô nhập liệu (`TiktokId.COMMENT_INPUT`), chọn ô cuối cùng (đang ở trạng thái có thể click).
 * Sau đó, mô phỏng gõ một chuỗi nội dung ngẫu nhiên (có chứa lỗi chính tả để trông giống người dùng thực hơn) và thoát.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun addComment(
    context: SceneExecutionContext
) = defineAction("Add Comment", context) {
    findAll(id(TiktokId.COMMENT_INPUT)).let { textFields ->
        humanType(textFields.last {it.isClickable}, generateComment(enableTypos = true))
        wait(random(100, 1500))
        pressBack()
    }
    endAction()
}

/**
 * Nhập từ khóa tìm kiếm và kích hoạt hành động tìm kiếm trên ứng dụng TikTok.
 *
 * Quy trình thực hiện:
 * 1. Tìm ô nhập liệu tìm kiếm thông qua ID `TiktokId.SEARCH_BAR`.
 * 2. Thực hiện tap vào ô nhập liệu, đợi 500ms, sau đó mô phỏng gõ phím kiểu người dùng ([humanType]) để điền từ khóa [keyword].
 * 3. Tìm nút Tìm kiếm thông qua ID `TiktokId.SEARCH_BUTTON` và tap vào để thực thi truy vấn tìm kiếm.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param keyword Chuỗi từ khóa cần tìm kiếm trên TikTok.
 */
fun typeSearchKeyword(context: SceneExecutionContext, keyword: String) =
    defineAction("Typing keyword", context) {
        find(id(TiktokId.SEARCH_BAR))?.let {
            tap(it)
            wait(500)
            humanType(it, keyword)
            wait(200)
        }
        find(id(TiktokId.SEARCH_BUTTON))?.let {
            tap(it)
            wait(1000)
        }
        endAction()
    }

/**
 * Chuyển sang tab Video trong kết quả tìm kiếm và chọn ngẫu nhiên một video để phát.
 *
 * Quy trình thực hiện:
 * 1. Tìm và tap vào tab "Video" (`TiktokText.VIDEO_TAB`). Nếu tab này không được chọn (isSelected = false), kết thúc hành động.
 * 2. Có xác suất 20% thực hiện cuộn màn hình lên (swipeUp) để xem thêm các video phía dưới.
 * 3. Tìm vùng danh sách kết quả (`TiktokId.SEARCH_RESULT_LIST`), tìm các view con thuộc lớp `android.view.View`.
 * 4. Lấy tối đa 4 video đầu tiên, chọn ngẫu nhiên một video và tap vào để phát, sau đó đợi 3000ms.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun selectVideoAfterSearch(context: SceneExecutionContext) = defineAction("Select Video", context) {
    find(text(TiktokText.VIDEO_TAB))?.let {
        tap(it)
        if (!it.isSelected) {
            endAction()
        }
    }
    loop {
        choose(5 to {
            swipeUp()
            wait(random(430, 1230))
        }, 5 to {
            find(id(TiktokId.SEARCH_RESULT_LIST))?.findObjects(clazz("android.view.View").toBySelector())
                .run {
                    this?.let {
                        tap(it.random())
                        waitUntil(id(TiktokId.USER_AVATAR), maxMs = 3000L)
                    }
                }
            endAction()
        })

    }
}

/**
 * Xử lý tự động đóng (dismiss) các hộp thoại hoặc màn hình hướng dẫn/pop-up không xác định xuất hiện đột xuất.
 *
 * Hàm sẽ kiểm tra sự hiện diện của hai nút:
 * - Nút "Đã hiểu" (`TiktokText.UNDERSTOOD`): Nếu tìm thấy, thực hiện tap để đóng.
 * - Nút "Không cho phép" (`TiktokText.NOT_ALLOWED`): Nếu tìm thấy, thực hiện tap để đóng/từ chối.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun onUnknowViewAction(context: SceneExecutionContext) =
    defineAction("Unknown View Action", context) {
        find(text(TiktokText.UNDERSTOOD))?.let {
            tap(it)
        }
        find(text(TiktokText.NOT_ALLOWED))?.let {
            tap(it)
        }
        endAction()
    }

/**
 * Xử lý hành động chia sẻ bài viết TikTok bằng cách chuyển sang tab Video trong màn hình chia sẻ.
 *
 * Quy trình thực hiện:
 * 1. Tìm nút tab "Video" (`TiktokText.VIDEO_TAB`) trên giao diện chia sẻ.
 * 2. Thực hiện tap vào tab đó, đợi một khoảng thời gian ngẫu nhiên từ 3000ms đến 5000ms rồi kết thúc hành động.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun onTiktokSharePostAction(context: SceneExecutionContext) =
    defineAction("Tiktok Share Post Action", context) {
        find(text(TiktokText.VIDEO_TAB))?.let {
            tap(it)
            waitUntil(selector = desc("Mẫu") and desc("Văn bản"),maxMs = 60000L)
            endAction()
        }
    }

/**
 * Nhấn chọn thêm âm thanh cho video trong quy trình đăng tải bài viết TikTok.
 *
 * Quy trình thực hiện:
 * 1. Tìm nút/văn bản Thêm âm thanh (`TiktokId.ADD_SOUND_TEXT`). Nếu text hiển thị của nó chính xác là "Thêm âm thanh" (`TiktokText.ADD_SOUND`), 
 *    tiến hành tap vào nút này để mở màn hình chọn nhạc và đợi 5000ms.
 * 2. Nếu nút trên không khả dụng hoặc đã có nhạc, tìm nút Tiếp (`TiktokId.NEXT_BUTTON`), tap vào đó và đợi ngẫu nhiên từ 3000ms đến 5000ms.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun tapToAddMusic(context: SceneExecutionContext) = defineAction("Tap To Add Music", context) {
    find(id(TiktokId.ADD_SOUND_TEXT) or id("com.ss.android.ugc.trill:id/zct"))?.let {
        if (it.text == TiktokText.ADD_SOUND) {
            tap(it)
            wait(5000)
            endAction()
        }
    }
    find(text("Tiếp"))?.let {
        tap(it)
        wait(random(3000, 5000))
    }
    endAction()
}

fun tapAutoCut(context: SceneExecutionContext, action: Action) = defineAction("Tap AutoCut", context) {
    find(desc("Mẫu"))?.let {
        tap(it)
        action()
        wait(random(5000, 10000))
    }
}

fun tapText(context: SceneExecutionContext, text: String, action: Action) = defineAction("Tap Text", context) {
    find(desc("Văn bản"))?.let {
        context.device.executeShellCommand("ime set com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME")
        tap(it)
        wait(random(1500, 3000))
        on(clazz("android.widget.EditText")) { editText ->
            humanType(editText, text)
            action()
            wait(random(1500, 3000))
            on(desc("Text")) { text -> tap(text)}
            wait(random(1500, 3000))
            pressBack()
        }
    }
}

/**
 * Nhấn nút Đăng để hoàn tất việc đăng tải video, đồng thời thực thi callback đi kèm.
 *
 * Quy trình thực hiện:
 * 1. Tìm nút có nhãn "Đăng" (`TiktokText.POST`) và thực hiện tap vào nút đó.
 * 2. Gọi hàm callback [action] được truyền vào để thực hiện các thao tác hậu kỳ hoặc xác minh tiếp theo.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param action Callback hành động suspend sẽ được gọi ngay sau khi nhấn nút Đăng.
 */
fun tapToUpload(context: SceneExecutionContext, action: Action) =
    defineAction("Tap To Upload", context) {
        tap(find(text(TiktokText.POST)))
        action()
        endAction()
    }

/**
 * Chọn ngẫu nhiên một bài hát từ danh sách nhạc đề xuất của TikTok.
 *
 * Quy trình thực hiện:
 * 1. Tìm danh sách bài hát thông qua ID `TiktokId.MUSIC_LIST`.
 * 2. Lấy các item con đại diện cho các dòng chứa bài hát (`android.widget.LinearLayout`).
 * 3. Chọn ngẫu nhiên một item bài hát và thực hiện tap vào đó.
 * 4. Đợi một khoảng thời gian ngẫu nhiên từ 3000ms đến 5000ms để áp dụng nhạc, sau đó nhấn phím Back để quay lại.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun selectRandomMusic(context: SceneExecutionContext) =
    defineAction("Select random music", context) {
        find(id(TiktokId.MUSIC_LIST))?.findObjects(clazz("android.widget.LinearLayout").toBySelector())
            .run {
                tap(this?.random())
                wait(random(3000, 5000))
                pressBack()
            }
        endAction()
    }

/**
 * Nhập mô tả (caption) của video và nhấn nút Đăng để bắt đầu tải bài viết lên TikTok.
 *
 * Quy trình thực hiện:
 * 1. Lấy thông tin caption từ đối số (`context.args.getString("caption")`).
 * 2. Tìm ô nhập caption thông qua ID `TiktokId.CAPTION_INPUT`.
 * 3. Gõ nội dung caption kèm dấu cách phía sau bằng phương thức [humanType], sau đó đợi ngẫu nhiên từ 3000ms đến 5000ms.
 * 4. Tìm nút Đăng (`TiktokText.POST`) và tap vào.
 * 5. Chờ một khoảng thời gian dài ngẫu nhiên từ 20000ms đến 40000ms cho quá trình tải lên hoàn tất.
 * 6. Cuối cùng, thực hiện nhấn phím Home (`pressHome`) và dừng tiến trình kiểm thử (`stop`).
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun typeCaption(context: SceneExecutionContext) = defineAction("Type Caption", context) {
    val caption = context.args.getString("caption")!!
    find(clazz("android.widget.EditText"))?.let {
        humanType(it, "$caption ")
        wait(random(3000, 5000))
        find(text(TiktokText.POST))?.let { post ->
            tap(post)
            wait(random(20000, 40000))
            pressHome()
            stop()
        }
    }
    endAction()
}

/**
 * Chuyển sang tab Người dùng tại trang kết quả tìm kiếm và chọn tài khoản trùng khớp với tên đăng nhập (username) được chỉ định.
 *
 * Quy trình thực hiện:
 * 1. Tìm tab "Người dùng" (`TiktokText.USER_TAB`) và tap vào đó, đợi ngẫu nhiên từ 3000ms đến 5000ms.
 * 2. Nếu tab này không được chọn thành công, kết thúc hành động.
 * 3. Tìm toàn bộ các view hiển thị tên người dùng (`TiktokId.SEARCH_USERNAME`) trên danh sách kết quả.
 * 4. Lấy phần tử cuối cùng có văn bản khớp chính xác với [username] (sau khi đã loại bỏ khoảng trắng thừa).
 * 5. Nếu không tìm thấy người dùng phù hợp, gọi callback [onNoUser].
 * 6. Nếu tìm thấy, thực hiện tap vào tài khoản đó để mở trang cá nhân của họ và đợi ngẫu nhiên từ 1000ms đến 3000ms.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param username Tên tài khoản TikTok cần tìm kiếm và bấm vào.
 * @param onNoUser Callback hành động suspend được gọi khi không tìm thấy bất kỳ người dùng nào khớp với [username].
 */
fun selectUser(
    context: SceneExecutionContext, username: String, onNoUser: Action
) = defineAction("Select User", context) {
    find(text(TiktokText.USER_TAB))?.let {
        tap(it)
        waitUntil(id(TiktokId.SEARCH_USERNAME), maxMs = random(3000L, 5000L))
        if (!it.isSelected) {
            endAction()
        }
    }
    findAll(id(TiktokId.SEARCH_USERNAME)).findLast { it.text.trim() == username.trim() }
        .let {
            if (it == null) {
                onNoUser()
            } else {
                tap(it)
                waitUntil(id(TiktokId.PROFILE_VIDEO_GRID), maxMs = random(1000L, 3000L))
            }
        }
    endAction()
}

/**
 * Lấy danh sách video từ lưới bài viết trên trang hồ sơ (Profile) của người dùng hiện tại và thực hiện hành động callback chỉ định.
 *
 * Quy trình thực hiện:
 * 1. Tìm lưới video trên Profile thông qua ID `TiktokId.PROFILE_VIDEO_GRID`.
 * 2. Tìm tất cả các phần tử video con thông qua ID `TiktokId.PROFILE_VIDEO_ITEM`.
 * 3. Nếu danh sách video trống hoặc null, gọi callback [onNoVideos] để xử lý tình huống không có video nào.
 * 4. Nếu có video, thực hiện callback [action] bằng cách truyền vào danh sách video tìm được để xử lý tiếp.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param onNoVideos Callback hành động suspend được gọi khi trang cá nhân không có video nào.
 * @param action Callback hành động suspend nhận danh sách các đối tượng video (`List<UiObject2>`) để tiếp tục xử lý.
 */
fun onChooseVideo(
    context: SceneExecutionContext,
    onNoVideos: Action,
    action: suspend ActionBuilder.(List<UiObject2>) -> Unit
) = defineAction("Choose Video", context) {
    find(clazz("android.widget.GridView")).let { grid ->
        val videoItems = grid?.children?.mapNotNull {
            it.children.lastOrNull()?.children?.lastOrNull()
        } ?: emptyList()
        if (videoItems.isEmpty()) {
            onNoVideos()
        } else {
            action(videoItems)
        }
    }
}

/**
 * Mở menu tùy chọn hoặc chia sẻ của video đang phát hiện tại.
 *
 * Quy trình thực hiện:
 * 1. Tìm nút Chia sẻ/Xem thêm (`TiktokId.SHARE_BUTTON`) trên màn hình xem video.
 * 2. Tap vào nút đó để mở menu tùy chọn/chia sẻ của video.
 * 3. Đợi một khoảng thời gian ngẫu nhiên từ 1000ms đến 1500ms để menu hiển thị đầy đủ.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun openVideoMenu(
    context: SceneExecutionContext
) = defineAction("Open Video Menu", context) {
    find(id(TiktokId.SHARE_BUTTON))?.let {
        tap(it)
        wait(random(1000, 1500))
    }
    endAction()
}

/**
 * Cuộn ngang bảng chọn chia sẻ để tìm và nhấn nút Xóa video.
 *
 * Quy trình thực hiện:
 * 1. Tìm vùng chứa danh sách các tùy chọn chia sẻ thông qua ID `TiktokId.SHARE_OPTIONS_LIST`.
 * 2. Thực hiện cuộn ngang sang phía bên phải (Direction.RIGHT) với tỷ lệ cuộn 0.8f để hiển thị các nút chức năng ẩn phía sau (như nút Xóa).
 * 3. Đợi ngẫu nhiên từ 1000ms đến 1500ms.
 * 4. Tìm nút có nhãn "Xóa" (`TiktokText.DELETE`) và tap vào, sau đó tiếp tục đợi ngẫu nhiên từ 1000ms đến 1500ms.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun swipeToChooseDelete(
    context: SceneExecutionContext
) = defineAction("Swipe to choose delete", context) {
    find(id(TiktokId.SHARE_OPTIONS_LIST))?.scroll(Direction.RIGHT, 0.8f)
    wait(random(1000, 1500))
    find(text(TiktokText.DELETE))?.let {
        tap(it)
        wait(random(1000, 1500))
    }
    endAction()
}

/**
 * Nhấn nút xác nhận Xóa và Đăng lại trên popup/bottom sheet của TikTok.
 *
 * Quy trình thực hiện:
 * 1. Tìm nút "Xóa và Đăng lại" thông qua ID `TiktokId.DELETE_AND_REPOST_BUTTON`.
 * 2. Nếu tìm thấy nút này, thực hiện tap vào nó.
 * 3. Đợi ngẫu nhiên từ 2000ms đến 5000ms để quá trình xử lý diễn ra hoàn tất trước khi kết thúc hành động.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 */
fun tapDeleteAndRepost(
    context: SceneExecutionContext
) = defineAction("Delete and repost", context) {
    find(id(TiktokId.DELETE_AND_REPOST_BUTTON)).let {
        if (it != null) {
            tap(it)
        }
        wait(random(2000, 3000))
        endAction()
    }
}

/**
 * Xác nhận hành động Xóa (chỉ xóa, không đăng lại) trên popup hỏi ý kiến đăng lại.
 *
 * Quy trình thực hiện:
 * 1. Tìm nút Xóa trong popup thông qua ID `TiktokId.DELETE_IN_REPOST_POPUP`.
 * 2. Nếu tìm thấy nút này, thực hiện tap vào để xác nhận xóa.
 * 3. Gọi hàm callback [action] truyền vào để tiếp tục quy trình xử lý sau khi xóa.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param action Callback hành động suspend được gọi sau khi bấm nút Xóa.
 */
fun tapDeleteInRepostPopup(
    context: SceneExecutionContext,
    action: Action
) = defineAction("Delete in repost popup", context) {
    find(id(TiktokId.DELETE_IN_REPOST_POPUP))?.let {
        tap(it)
        action()
    }
    endAction()
}

/**
 * Xác nhận xóa bài viết vĩnh viễn trên popup xác nhận xóa của ứng dụng TikTok.
 *
 * Quy trình thực hiện:
 * 1. Tìm nút xác nhận Xóa thông qua ID `TiktokId.CONFIRM_DELETE_BUTTON`.
 * 2. Nếu tìm thấy, thực hiện tap vào nút đó để xóa vĩnh viễn video.
 * 3. Đợi ngẫu nhiên từ 2000ms đến 5000ms để hệ thống thực thi xóa và cập nhật giao diện.
 * 4. Gọi callback [action] truyền vào để tiếp tục luồng xử lý tiếp theo.
 *
 * @param context Ngữ cảnh thực thi hành động (`SceneExecutionContext`).
 * @param action Callback hành động suspend được gọi sau khi hoàn thành các bước bấm nút và chờ đợi.
 */
fun tapDeleteVideo(
    context: SceneExecutionContext,
    action: Action
) = defineAction("Delete post", context) {
    find(id(TiktokId.CONFIRM_DELETE_BUTTON))?.let { deleteButton ->
        tap(deleteButton)
        wait(random(2000, 5000))
        action()
    }
    endAction()
}
