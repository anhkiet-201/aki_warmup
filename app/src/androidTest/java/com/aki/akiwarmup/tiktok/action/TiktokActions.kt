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
        on(id(TiktokId.VIDEO_DESC)) { desc ->
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
            on(text(TiktokText.RECOMMENDED_PROMOTION)) {
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
                    find(id(TiktokId.LIKE_BUTTON))?.let {
                        rate.consume(RateType.LIKE)
                        if (!it.isSelected) {
                            tap(it)
                            wait(random(min = 1000, max = 3000))
                        }
                    }
                },
                rate[RateType.FAVORITE] to {
                    find(id(TiktokId.FAVORITE_BUTTON))?.let {
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
                        find(id(TiktokId.COMMENT_BUTTON))?.let { commentButton ->
                            tap(commentButton)
                            wait(random(100, 1500))
                            sometimes(20) {
                                swipeUp()
                            }
                            find(id(TiktokId.COMMENT_INPUT))?.let { textField ->
                                humanType(textField, generateComment(enableTypos = true))
                                wait(random(100, 1500))
                                pressBack()
                                wait(random(100, 1500))
                                find(desc(TiktokDesc.POST_COMMENT_BUTTON))?.let { postButton ->
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
                        endAction()
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
 * Mở tab **Video** và chọn ngẫu nhiên video để phát.
 */
fun selectVideoAfterSearch(context: SceneExecutionContext) = defineAction("Select Video", context) {
    find(text(TiktokText.VIDEO_TAB))?.let {
        tap(it)
        if (!it.isSelected) {
            endAction()
        }
    }
    sometimes(20) {
        swipeUp()
    }
    find(id(TiktokId.SEARCH_RESULT_LIST))?.findObjects(clazz("android.view.View").toBySelector())
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
        find(text(TiktokText.UNDERSTOOD))?.let {
            tap(it)
        }
        find(text(TiktokText.NOT_ALLOWED))?.let {
            tap(it)
        }
        endAction()
    }

/**
 * Nhấn chọn nút **Video** trong màn hình share post.
 */
fun onTiktokSharePostAction(context: SceneExecutionContext) =
    defineAction("Tiktok Share Post Action", context) {
        find(text(TiktokText.VIDEO_TAB))?.let {
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
    find(id(TiktokId.ADD_SOUND_TEXT))?.let {
        if (it.text == TiktokText.ADD_SOUND) {
            tap(it)
            wait(5000)
            endAction()
        }
    }
    find(id(TiktokId.NEXT_BUTTON))?.let {
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
        tap(find(text(TiktokText.POST)))
        action()
        endAction()
    }

/**
 * Chọn nhạc ngẫu nhiên từ danh sách.
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
 * Nhập caption rồi nhấn **Đăng**.
 */
fun typeCaption(context: SceneExecutionContext) = defineAction("Type Caption", context) {
    val caption = context.args.getString("caption")!!
    find(id(TiktokId.CAPTION_INPUT))?.let {
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
 * Chọn tab **Người dùng** rồi tap vào user trùng username.
 * @param username tên tài khoản TikTok cần tìm
 * @param onNoUser callback khi không tìm thấy user
 */
fun selectUser(
    context: SceneExecutionContext, username: String, onNoUser: Action
) = defineAction("Select User", context) {
    find(text(TiktokText.USER_TAB))?.let {
        tap(it)
        wait(random(3000, 5000))
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
    find(id(TiktokId.PROFILE_VIDEO_GRID))?.findObjects(id(TiktokId.PROFILE_VIDEO_ITEM).toBySelector())
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
    find(id(TiktokId.SHARE_BUTTON))?.let {
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
    find(id(TiktokId.SHARE_OPTIONS_LIST))?.scroll(Direction.RIGHT, 0.8f)
    wait(random(1000, 1500))
    find(text(TiktokText.DELETE))?.let {
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
    find(id(TiktokId.DELETE_AND_REPOST_BUTTON)).let {
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
    find(id(TiktokId.DELETE_IN_REPOST_POPUP))?.let {
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
    find(id(TiktokId.CONFIRM_DELETE_BUTTON))?.let { deleteButton ->
        tap(deleteButton)
        wait(random(2000, 5000))
        action()
    }
    endAction()
}
