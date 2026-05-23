package com.aki.akiwarmup

import android.util.Log
import com.aki.akiwarmup.core.dsl.ActionBuilder
import com.aki.akiwarmup.core.dsl.SceneExecutionContext
import com.aki.akiwarmup.core.dsl.clazz
import com.aki.akiwarmup.core.dsl.defineAction
import com.aki.akiwarmup.core.dsl.desc
import com.aki.akiwarmup.core.dsl.id
import com.aki.akiwarmup.core.dsl.text
import com.aki.akiwarmup.random.generateComment

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
fun onWatchVideo(
    context: SceneExecutionContext,
    rate: AutoRate = AutoRate(),
    id: String = "Home",
    onExit: suspend ActionBuilder.() -> Unit
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
            choose(
                rate.swipeRate to {
                    rate.reset()
                    loopCount = 1
                    swipeUp()
                },
                rate.likeRate to {
                    find(id("com.ss.android.ugc.trill:id/fhc"))?.let {
                        rate.onLike()
                        if (!it.isSelected) {
                            tap(it)
                            wait(random(min = 1000, max = 3000))
                        }
                    }
                },
                rate.favoriteRate to {
                    find(id("com.ss.android.ugc.trill:id/h_9"))?.let {
                        rate.onFavorite()
                        if (!it.isSelected) {
                            tap(it)
                            wait(random(min = 1000, max = 3000))
                        }
                    }
                },
                rate.commentRate to {
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
                },
                rate.rePostRate to {
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
                },
                rate.copyLinkRate to {
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
                },
                rate.exitRate to {
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
 * Nhập từ khóa vào thanh tìm kiếm trong trang SEARCH sau đó nhấn nút **Tìm kiếm**
 * @param context là ngữ cảnh của Scene hiện tại
 * */
fun onTypeSearchKeyword(context: SceneExecutionContext) = defineAction("Typing keyword", context) {
    find(id("com.ss.android.ugc.trill:id/gz8"))?.let {
        tap(it)
        wait(500)
        val keyword = context.args.getString("keyword")
        humanType(it, keyword ?: keyWorlds.random())
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
fun onSelectVideoAfterSearch(context: SceneExecutionContext) =
    defineAction("Select Video", context) {
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
 * Nhập từ khóa vào thanh tìm kiếm trong trang SEARCH sau đó nhấn nút **Tìm kiếm**
 * @param context là ngữ cảnh của Scene hiện tại
 * */
fun onTypeSearchKeyword(context: SceneExecutionContext) = defineAction("Typing keyword", context) {
    find(id("com.ss.android.ugc.trill:id/gz8"))?.let {
        tap(it)
        wait(500)
        val keyword = context.args.getString("keyword")
        humanType(it, keyword ?: keyWorlds.random())
        wait(200)
    }
    find(id("com.ss.android.ugc.trill:id/trq"))?.let {
        tap(it)
        wait(1000)
    }
    endAction()
}




