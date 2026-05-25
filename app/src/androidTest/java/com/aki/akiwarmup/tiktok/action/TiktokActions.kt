package com.aki.akiwarmup.tiktok.action

import android.util.Log
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
    "việc làm bình dương ttnhr"
)

/**
 * Lướt xem video, thực hiện các thao tác như yêu thích, bình luận, theo chủ đề.
 * @param context ngữ cảnh Scene hiện tại
 * @param rate tỉ lệ hành động (swipe, like, comment, v.v.)
 * @param onExit callback khi hoàn thành lướt xem video
 */
fun watchVideo(
    context: SceneExecutionContext,
    rate: AutoRate = AutoRate(),
    id: String = "Watch Video",
    onExit: Action
) = defineAction(id, context) {
    var loopCount = 1
    loop {
        on(id("com.ss.android.ugc.trill:id/desc")) { desc ->
            // Cache text một lần duy nhất — UiObject2.text là IPC call tốn kém,
            // tránh gọi lặp lại trong captionKeyword.none{} và nhánh COMMENT.
            val descText = desc?.text
            descText?.let {
                Log.i("AkiFramework", "Desc: $descText")
                if (captionKeyword.none { kw -> it.toLowerCase(Locale.current).contains(kw.toLowerCase(Locale.current)) }) {
                    Log.i("AkiFramework", "Không đúng nội dung")
                    rate.swipeBias()
                } else {
                    Log.i("AkiFramework", "Đúng nội dung")
                    wait(random(3000, 30000 / loopCount))
                }
            }
            on(text("Quảng bá đề xuất")) {
                if (it != null) {
                    swipeUp()
                    endAction()
                }
            }
            choose(
                rate[RateType.SWIPE] to {
                    rate.reset()
                    loopCount = 1
                    swipeUp()
                },
                rate[RateType.LIKE] to {
                    find(id("com.ss.android.ugc.trill:id/fhc"))?.let {
                        rate.consume(RateType.LIKE)
                        if (!it.isSelected) {
                            tap(it)
                            wait(random(min = 1000, max = 3000))
                        }
                    }
                },
                rate[RateType.FAVORITE] to {
                    find(id("com.ss.android.ugc.trill:id/h_9"))?.let {
                        rate.consume(RateType.FAVORITE)
                        if (!it.isSelected) {
                            tap(it)
                            wait(random(min = 1000, max = 3000))
                        }
                    }
                },
                rate[RateType.COMMENT] to {
                    rate.consume(RateType.COMMENT)
                    if (descText?.contains("#ttnhr") == true) {
                        find(id("com.ss.android.ugc.trill:id/e0m"))?.let { commentButton ->
                            tap(commentButton)
                            wait(random(100, 1500))
                            sometimes(20) {
                                swipeUp()
                            }
                            find(id("com.ss.android.ugc.trill:id/e02"))?.let { textField ->
                                humanType(textField, generateComment(enableTypos = true))
                                wait(random(100, 1500))
                                pressBack()
                                wait(random(100, 1500))
                                find(desc("@2131953937"))?.let { postButton ->
                                    tap(postButton)
                                    wait(random(100, 1500))
                                }
                            }
                            pressBack()
                        }
                    }
                    endAction()
                },
                rate[RateType.RE_POST] to {
                    find(id("com.ss.android.ugc.trill:id/ubv"))?.let {
                        rate.consume(RateType.RE_POST)
                        tap(it)
                        wait(random(1000, 1500))
                        find(text("Đăng lại")).let { repost ->
                            if (repost == null) {
                                pressBack()
                            } else {
                                tap(repost)
                                wait(random(1000, 1500))
                            }
                        }
                        endAction()
                    }
                },
                rate[RateType.COPY_LINK] to {
                    find(id("com.ss.android.ugc.trill:id/ubv"))?.let {
                        rate.consume(RateType.COPY_LINK)
                        tap(it)
                        wait(random(1000, 1500))
                        find(text("Sao chép Liên kết"))?.let { repost ->
                            tap(repost)
                            wait(random(1000, 1500))
                        }
                        endAction()
                    }
                },
                rate[RateType.EXIT] to {
                    this.onExit()
                    rate.reset()
                    endAction()
                }
            )
        }
        loopCount++
    }
}

/**
 * Nhập từ khóa vào thanh tìm kiếm rồi nhấn **Tìm kiếm**.
 */
fun typeSearchKeyword(context: SceneExecutionContext, keyword: String) =
    defineAction("Typing keyword", context) {
        find(id("com.ss.android.ugc.trill:id/gz8"))?.let {
            tap(it)
            wait(500)
            humanType(it, keyword)
            wait(200)
        }
        find(id("com.ss.android.ugc.trill:id/trq"))?.let {
            tap(it)
            wait(1000)
        }
        endAction()
    }

/**
 * Mở tab **Video** và chọn ngẫu nhiên video để phát.
 */
fun selectVideoAfterSearch(context: SceneExecutionContext) = defineAction("Select Video", context) {
    find(text("Video"))?.let {
        tap(it)
        if (!it.isSelected) {
            endAction()
        }
    }
    sometimes(20) {
        swipeUp()
    }
    find(id("com.ss.android.ugc.trill:id/m_7"))?.findObjects(clazz("android.view.View").toBySelector())
        .run {
            this?.take(4)?.let {
                tap(it.random())
                wait(3000)
            }
        }
    endAction()
}

/**
 * Xử lý các màn hình không mong muốn (dialog cần dismiss).
 */
fun onUnknowViewAction(context: SceneExecutionContext) =
    defineAction("Unknown View Action", context) {
        find(text("Đã hiểu"))?.let {
            tap(it)
        }
        find(text("Không cho phép"))?.let {
            tap(it)
        }
        endAction()
    }

/**
 * Nhấn chọn nút **Video** trong màn hình share post.
 */
fun onTiktokSharePostAction(context: SceneExecutionContext) =
    defineAction("Tiktok Share Post Action", context) {
        find(text("Video"))?.let {
            tap(it)
            wait(random(3000, 5000))
            endAction()
        }
        endAction()
    }

/**
 * Nhấn nút **Thêm âm thanh**.
 * Nếu đã có nhạc thì nhấn **Tiếp**.
 */
fun tapToAddMusic(context: SceneExecutionContext) = defineAction("Tap To Add Music", context) {
    find(id("com.ss.android.ugc.trill:id/zo6"))?.let {
        if (it.text == "Thêm âm thanh") {
            tap(it)
            wait(5000)
            endAction()
        }
    }
    find(id("com.ss.android.ugc.trill:id/ond"))?.let {
        tap(it)
        wait(random(3000, 5000))
    }
    endAction()
}

/**
 * Nhấn nút **Đăng**.
 * @param action callback sau khi nhấn đăng
 */
fun tapToUpload(context: SceneExecutionContext, action: Action) =
    defineAction("Tap To Upload", context) {
        tap(find(text("Đăng")))
        action()
        endAction()
    }

/**
 * Chọn nhạc ngẫu nhiên từ danh sách.
 */
fun selectRandomMusic(context: SceneExecutionContext) =
    defineAction("Select random music", context) {
        find(id("com.ss.android.ugc.trill:id/t96"))?.findObjects(clazz("android.widget.LinearLayout").toBySelector())
            .run {
                tap(this?.random())
                wait(random(3000, 5000))
                pressBack()
            }
        endAction()
    }

/**
 * Nhập caption rồi nhấn **Đăng**.
 */
fun typeCaption(context: SceneExecutionContext) = defineAction("Type Caption", context) {
    val caption = context.args.getString("caption")!!
    find(id("com.ss.android.ugc.trill:id/gfw"))?.let {
        humanType(it, "$caption ")
        wait(random(3000, 5000))
        find(text("Đăng"))?.let { post ->
            tap(post)
            wait(random(20000, 40000))
            pressHome()
            stop()
        }
    }
    endAction()
}

/**
 * Chọn tab **Người dùng** rồi tap vào user trùng username.
 * @param username tên tài khoản TikTok cần tìm
 * @param onNoUser callback khi không tìm thấy user
 */
fun selectUser(
    context: SceneExecutionContext, username: String, onNoUser: Action
) = defineAction("Select User", context) {
    find(text("Người dùng"))?.let {
        tap(it)
        wait(random(3000, 5000))
        if (!it.isSelected) {
            endAction()
        }
    }
    findAll(id("com.ss.android.ugc.trill:id/yi8")).findLast { it.text.trim() == username.trim() }
        .let {
            if (it == null) {
                onNoUser()
            } else {
                tap(it)
                wait(random(1000, 3000))
            }
        }
    endAction()
}

/**
 * Lấy danh sách videos từ trang Profile rồi gọi [action] với danh sách đó.
 * @param onNoVideos callback khi không có video nào
 * @param action callback nhận List<UiObject2> videos
 */
fun onChooseVideo(
    context: SceneExecutionContext,
    onNoVideos: Action,
    action: suspend ActionBuilder.(List<UiObject2>) -> Unit
) = defineAction("Choose Video", context) {
    find(id("com.ss.android.ugc.trill:id/hdm"))?.findObjects(id("com.ss.android.ugc.trill:id/z9y").toBySelector())
        .let {
            if (it?.isEmpty() != false) {
                onNoVideos()
            } else {
                action(it)
            }
            endAction()
        }
}

/**
 * Mở menu tùy chọn video (nút share/more).
 */
fun openVideoMenu(
    context: SceneExecutionContext
) = defineAction("Open Video Menu", context) {
    find(id("com.ss.android.ugc.trill:id/ubv"))?.let {
        tap(it)
        wait(random(1000, 1500))
    }
    endAction()
}

/**
 * Cuộn phải tới nút **Xóa** trên Share screen rồi tap vào.
 */
fun swipeToChooseDelete(
    context: SceneExecutionContext
) = defineAction("Swipe to choose delete", context) {
    find(id("com.ss.android.ugc.trill:id/vv"))?.scroll(Direction.RIGHT, 0.8f)
    wait(random(1000, 1500))
    find(text("Xóa"))?.let {
        tap(it)
        wait(random(1000, 1500))
    }
    endAction()
}

/**
 * Nhấn nút **Xóa và Đăng lại** trong Repost popup.
 */
fun tapDeleteAndRepost(
    context: SceneExecutionContext
) = defineAction("Delete and repost", context) {
    find(id("com.ss.android.ugc.trill:id/sbo")).let {
        if (it != null) {
            tap(it)
        }
        wait(random(2000, 5000))
        endAction()
    }
}

/**
 * Nhấn nút **Xóa** trong Repost popup (chỉ xóa, không đăng lại).
 */
fun tapDeleteInRepostPopup(
    context: SceneExecutionContext,
    action: Action
) = defineAction("Delete in repost popup", context) {
    find(id("com.ss.android.ugc.trill:id/f9z"))?.let {
        tap(it)
        action()
    }
    endAction()
}

/**
 * Nhấn nút **Xóa** trong Delete popup để xác nhận xóa video.
 */
fun tapDeleteVideo(
    context: SceneExecutionContext,
    action: Action
) = defineAction("Delete post", context) {
    find(id("com.ss.android.ugc.trill:id/wk"))?.let { deleteButton ->
        tap(deleteButton)
        wait(random(2000, 5000))
        action()
    }
    endAction()
}
