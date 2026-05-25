package com.aki.akiwarmup

import android.util.Log
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
    "vieclam"
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
 *Lướt xem video, thực hiện các thao tác như yêu thích, bình luận, theo chủ đề
 * @param context là ngữ cảnh của Scene hiện tại
 * @param onExit là callback khi hoàn thành lướt xem video
 * */
fun watchVideo(
    context: SceneExecutionContext,
    rate: AutoRate = AutoRate(),
    id: String = "Home",
    onExit: Action
) = defineAction(id, context) {
    var loopCount = 1
    loop {
        on(text("Quảng bá đề xuất")) {
            if (it != null) {
                swipeUp()
                endAction()
            }
        }
        on(id("com.ss.android.ugc.trill:id/desc")) { desc ->
            desc?.let { element ->
                Log.i("AkiFramework", "Desc: ${element.text}")
                if (captionKeyword.none { element.text.contains(it) }) {
                    rate.swipeBias()
                } else {
                    wait(random(500, 20000 / loopCount))
                }
            }
            choose(rate.swipeRate to {
                rate.reset()
                loopCount = 1
                swipeUp()
            }, rate.likeRate to {
                find(id("com.ss.android.ugc.trill:id/fhc"))?.let {
                    rate.onLike()
                    if (!it.isSelected) {
                        tap(it)
                        wait(random(min = 1000, max = 3000))
                    }
                }
            }, rate.favoriteRate to {
                find(id("com.ss.android.ugc.trill:id/h_9"))?.let {
                    rate.onFavorite()
                    if (!it.isSelected) {
                        tap(it)
                        wait(random(min = 1000, max = 3000))
                    }
                }
            }, rate.commentRate to {
                rate.onComment()
                if (desc?.text?.contains("#ttnhr") ?: false) {
                    find(id("com.ss.android.ugc.trill:id/e0m"))?.let { commentButon ->
                        tap(commentButon)
                        wait(random(100, 1500))
                        sometimes(5f) {
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
            }, rate.rePostRate to {
                find(id("com.ss.android.ugc.trill:id/ubv"))?.let {
                    rate.onRePost()
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
            }, rate.copyLinkRate to {
                find(id("com.ss.android.ugc.trill:id/ubv"))?.let {
                    rate.onCopyLink()
                    tap(it)
                    wait(random(1000, 1500))
                    find(text("Sao chép Liên kết"))?.let { repost ->
                        tap(repost)
                        wait(random(1000, 1500))
                    }
                    endAction()
                }
            }, rate.exitRate to {
                this.onExit()
                rate.reset()
                endAction()
            })
        }
        loopCount++
    }
}


/**
 * Nhập từ khóa vào thanh tìm kiếm trong trang SEARCH sau đó nhấn nút **Tìm kiếm**
 * @param context là ngữ cảnh của Scene hiện tại
 * */
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
 * Mở tab **Video** và chọn ngẫu nhiên video để phát
 * @param context là ngữ cảnh của Scene hiện tại
 * */
fun selectVideoAfterSearch(context: SceneExecutionContext) = defineAction("Select Video", context) {
    find(text("Video"))?.let {
        tap(it)
        if (!it.isSelected) {
            endAction()
        }
    }
    sometimes(0.2f) {
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
 * Giái quết các mang hình không mong muốn
 * @param context là ngữ cảnh của Scene hiện tại
 * */
fun onUnknowViewAction(context: SceneExecutionContext) =
    defineAction("Unknow View Action", context) {
        find(text("Đã hiểu"))?.let {
            tap(it)
        }
        find(text("Không cho phép"))?.let {
            tap(it)
        }
        endAction()
    }

/**
 * Nhấn chọn nút **Video**
 * @param context là ngữ cảnh của Scene hiện tại
 * */
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
 * Nhấn chọn nút **Thêm nhạc**
 * Nếu đã có nhạc thì nhấn nút **Tiếp**
 * @param context là ngữ cảnh của Scene hiện tại
 * */
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
 * Nhấn chọn nút **Đăng**
 * @param context là ngữ cảnh của Scene hiện tại
 * @param action là callback sau khi nhấn nút đăng
 * */
fun tapToUpload(context: SceneExecutionContext, action: Action) =
    defineAction("Tap To Upload", context) {
        tap(find(text("Đăng")))
        action()
        endAction()
    }


/**
 * Chọn nhạc ngẫu nhiên
 * @param context là ngữ cảnh của Scene hiện tại
 * */
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
 * Nhập caption sau đó nhấn nút **Đăng**
 * @param context là ngữ cảnh của Scene hiện tại
 * */
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
 * Chọn tab *User* sau đó chọn User
 * @param context là ngữ cảnh của Scene hiện tại
 * @param username là id người dùng tiktok
 * @param onNoUser là callback được gọi khi không tìm thấy user
 * */
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
 * Thực hiện hành động trên danh sách các video của *Profile*
 * @param context là ngữ cảnh của Scene hiện tại
 * @param onNoVideos là callback khi không có video nào
 * @param action là callback được gọi khi có danh sách videos
 * */
fun onChooseVideo(
    context: SceneExecutionContext,
    onNoVideos: Action,
    action: suspend ActionBuilder.(List<UiObject2>) -> Unit
) = defineAction("Choose Video", context) {
    find(id("com.ss.android.ugc.trill:id/hdm"))?.findObjects(id("com.ss.android.ugc.trill:id/z9y").toBySelector())
        .let {
            if (it?.isEmpty() ?: true) {
                onNoVideos()
            } else {
                action(it)
            }
            endAction()
        }
}

/**
 * Nhấp mở menu tùy chọn
 * @param context là ngữ cảnh của Scene hiện tại
 * */
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
 * Thực hiện cuộn phải tới nút *xóa* trên *Share screen*
 * @param context là ngữ cảnh của Scene hiện tại
 * */
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
 * Nhấn nút **Xóa và Đăng lại**
 * @param context là ngữ cảnh của Scene hiện tại
 * */
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
 * Nhấn nút **Xóa và Đăng lại**
 * @param context là ngữ cảnh của Scene hiện tại
 * */
fun tapDeleteInRepostPopup(
    context: SceneExecutionContext,
    action: Action
) = defineAction("Delete and repost", context) {
    find(id("com.ss.android.ugc.trill:id/f9z"))?.let {
        tap(it)
        action()
    }
    endAction()
}

/**
 * Nhấn nút **Xóa**
 * @param context là ngữ cảnh của Scene hiện tại
 * */
fun tapDeleteVideo(
    context: SceneExecutionContext,
    action: Action
) = defineAction("Delete post", context) {
    find(id("com.ss.android.ugc.trill:id/wk"))?.let { deleteButon ->
        tap(deleteButon)
        wait(random(2000, 5000))
        action()
    }
    endAction()
}





