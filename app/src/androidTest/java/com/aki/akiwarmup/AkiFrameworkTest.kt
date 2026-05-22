package com.aki.akiwarmup

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.type
import androidx.test.uiautomator.uiAutomator
import com.aki.akiwarmup.core.dsl.UnknownScreenPolicy
import com.aki.akiwarmup.core.dsl.clazz
import com.aki.akiwarmup.core.dsl.desc
import com.aki.akiwarmup.core.dsl.id
import com.aki.akiwarmup.core.dsl.runScene
import com.aki.akiwarmup.core.dsl.text
import com.aki.akiwarmup.core.dsl.textContains
import com.aki.akiwarmup.random.generateComment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AkiFrameworkTest {
    @Test
    fun warmUp() = runScene {
        val rate = AutoRate(_exitRate = 0.5f)
        scene("tiktok_warmup") {
            config {
                targetPackage = "com.ss.android.ugc.trill"
                onUnknownScreen = UnknownScreenPolicy.PRESS_BACK
                recoveryTimeoutMs = 15000L
            }

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

            handleUnknowScreen {
                Log.i("AkiFramework", "${context.restartCount}")
                if(this.context.restartCount > 3) {
                    this.context.stop("lỖI APP")
                }
            }

            launchApp()

            screen("Home") {
                detect {
                    all(
                        text("Trang chủ"),
                        id("com.ss.android.ugc.trill:id/user_avatar")
                    )
                }

                action("Lướt xem") {
                    loop {
                        wait(random(500, 20000))
                        choose(
                            rate.swipeRate to {
                                rate.reset()
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
                                find(id("com.ss.android.ugc.trill:id/desc"))?.let {
                                    rate.onComment()
                                    if (it.text.contains("#ttnhr")) {
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
                                    } else if (it.text.contains("…thêm")) {
                                        tap(it)
                                    }
                                    endAction()
                                }
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
                                find("com.ss.android.ugc.trill:id/jb1")?.let {
                                    tap(it)
                                    rate.reset()
                                    wait(300)
                                    endAction()
                                }
                            }
                        )
                    }
                }

            }

            screen("Search Screen") {
                detect {
                    has( id("com.ss.android.ugc.trill:id/gz8") and text("Tìm kiếm"))
                }
                action("Nhập từ khóa mặc định") {
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
                        endAction()
                    }
                }
            }

            screen("Search Result") {
                detect { has(id("com.ss.android.ugc.trill:id/viewpager_search")) }
                action("sd") {
                    find(text("Video"))?.let {
                        tap(it)
                        if (!it.isSelected) {
                            endAction()
                        }
                    }
                    sometimes(0.2f) {
                        swipeUp()
                    }
                    find(id("com.ss.android.ugc.trill:id/m_7"))?.findObjects(clazz("android.view.View").toBySelector()).run {
                        this?.take(4)?.let {
                            tap(it.random())
                            wait(3000)
                            endAction()
                        }
                    }
                }
            }

            screen("Video Search") {
                detect { has(text("Tìm kiếm") and id("com.ss.android.ugc.trill:id/user_avatar")) }

                action("Lướt xem Video Search") {
                    loop {
                        wait(random(1000, 20000))
                        choose(
                            rate.swipeRate to {
                                rate.reset()
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
                                find(id("com.ss.android.ugc.trill:id/desc"))?.let {
                                    rate.onComment()
                                    if (!it.text.contains("ttnhr")) {
                                        endAction()
                                    }
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
                                    endAction()
                                }
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
                                pressHome()
                                rate.reset()
                                this@scene.killApp()
                                stop()
                            }
                        )
                    }
                }
            }

            screen("Unknow") {
                detect {
                    any(text("Đã hiểu"), text("Không cho phép"))
                }

                action("Click") {
                    find(text("Đã hiểu"))?.let {
                        tap(it)
                    }
                    find(text("Không cho phép"))?.let {
                        tap(it)
                    }
                }
            }

            screen("expand comment") {
                detect {
                    has(id("com.ss.android.ugc.trill:id/dzt"))
                }

                action("comment") {
                    find(id("com.ss.android.ugc.trill:id/dzt"))?.let {
                        if (it.text.contains("ttnhr")) {
                            choose(
                                5f to {
                                    swipeUp()
                                },
                                5f to {
                                    find(id("com.ss.android.ugc.trill:id/kcz"))?.let { comment ->
                                        tap(comment)
                                        wait(random(100, 1500))
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
                                        pressBack()
                                        endAction()
                                    }
                                }
                            )
                        } else {
                            pressBack()
                            endAction()
                        }
                    }
                }
            }

        }

        loop {

        }
    }

    @Test
    fun autoPost() = runScene {
        scene("Auto Post") {
            config {
                targetPackage = "com.ss.android.ugc.trill"
                onUnknownScreen = UnknownScreenPolicy.PRESS_BACK
                recoveryTimeoutMs = 15000L
            }

            handleUnknowScreen {
                if(this.context.consecutiveUnknownScreens > 8) {
                    context.stop("Failure", -2)
                }
            }

            screen("Share on tiktok") {
                detect {
                    has(text("Chia sẻ lên TikTok"))
                }
               action("Click Video") {
                   find(text("Video"))?.let {
                       tap(it)
                       wait(random(3000, 5000))
                       endAction()
                   }
               }
            }

            screen("Video Edit") {
                detect {
                    has(id("com.ss.android.ugc.trill:id/zo6"))
                }

                action("Select Music") {
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
            }

            screen("Select Music") {
                detect {
                    has(id("com.ss.android.ugc.trill:id/t96"))
                }

                action("Choose Music") {
                    find(id("com.ss.android.ugc.trill:id/t96"))?.findObjects(clazz("android.widget.LinearLayout").toBySelector()).run {
                        tap(this?.random())
                        wait(random(3000, 5000))
                        pressBack()
                        endAction()
                    }
                }
            }

            screen("Type Caption") {
                detect {
                    has(id("com.ss.android.ugc.trill:id/gfw"))
                }

                action("Post") {
                    val caption = context.args.getString("caption")!!
                    find(id("com.ss.android.ugc.trill:id/gfw"))?.let {
                        humanType(it, "$caption ")
                        wait(random(3000,5000))
                        find(text("Đăng"))?.let { post ->
                            tap(post)
                            wait(random(20000, 40000))
                            pressHome()
                            stop()
                        }
                    }
                }
            }
        }
        loop {

        }
    }

    @Test
    fun seeding() = runScene {
        val rate = AutoRate(_swipeRate = 3f, _likeRate = 2f)
        scene("tiktok_seeding") {
            config {
                targetPackage = "com.ss.android.ugc.trill"
                onUnknownScreen = UnknownScreenPolicy.PRESS_BACK
                recoveryTimeoutMs = 15000L
            }

            handleUnknowScreen {
                Log.i("AkiFramework", "${context.restartCount}")
                if(this.context.restartCount > 3) {
                    this.context.stop("lỖI APP")
                }
            }

            launchApp()
            val keyword = context.args.getString("keyword")
            if (keyword == null) {
                context.stop("Wrong Keyword")
            }
            screen("Home") {
                detect {
                    all(
                        text("Trang chủ"),
                        id("com.ss.android.ugc.trill:id/user_avatar")
                    )
                }
                action("Lướt xem") {
                    loop {
                        wait(random(500, 20000))
                        choose(
                            rate.swipeRate to {
                                rate.reset()
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
                                find(id("com.ss.android.ugc.trill:id/desc"))?.let {
                                    rate.onComment()
                                    if (it.text.contains("#ttnhr")) {
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
                                    } else if (it.text.contains("…thêm")) {
                                        tap(it)
                                    }
                                    endAction()
                                }
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
                                find("com.ss.android.ugc.trill:id/jb1")?.let {
                                    tap(it)
                                    rate.reset()
                                    wait(300)
                                    endAction()
                                }
                            }
                        )
                    }
                }
            }

            screen("Search Screen") {
                detect {
                    has( id("com.ss.android.ugc.trill:id/gz8") and text("Tìm kiếm"))
                }
                action("Nhập từ khóa mặc định") {
                    find(id("com.ss.android.ugc.trill:id/gz8"))?.let {
                        tap(it)
                        wait(500)
                        humanType(it, keyword!!)
                        wait(200)
                    }
                    find(id("com.ss.android.ugc.trill:id/trq"))?.let {
                        tap(it)
                        wait(1000)
                        endAction()
                    }
                }
            }

            screen("Profile") {
                detect {
                    has(id("com.ss.android.ugc.trill:id/hdm"))
                }

                action("Choose Video") {
                    find(id("com.ss.android.ugc.trill:id/hdm"))?.findObjects(id("com.ss.android.ugc.trill:id/efq").toBySelector()).let {
                        if (it?.isEmpty() ?: true) {
                            stop("No Videos")
                        }
                        tap(it?.first())
                        endAction()
                    }
                }
            }

            screen("Search Result") {
                detect { has(id("com.ss.android.ugc.trill:id/viewpager_search")) }
                action("Chọn tap người dùng") {
                    find(text("Người dùng"))?.let {
                        tap(it)
                        wait(random(3000, 5000))
                        if (!it.isSelected) {
                            endAction()
                        }
                    }
                    findAll(id("com.ss.android.ugc.trill:id/yi8")).findLast { it.text.trim() == keyword!!.trim() }.let {
                        if (it == null) {
                            pressHome()
                            stop("Không tìm thấy User")
                        } else {
                            tap(it)
                            wait(random(1000, 3000))
                        }
                    }
                }
            }

            screen("Video Search") {
                detect { has(text("Tìm kiếm") and id("com.ss.android.ugc.trill:id/user_avatar")) }

                action("Lướt xem Video Search") {
                    loop {
                        wait(random(1000, 20000))
                        choose(
                            rate.swipeRate to {
                                rate.reset()
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
                                find(id("com.ss.android.ugc.trill:id/desc"))?.let {
                                    rate.onComment()
                                    if (it.text.contains("#ttnhr")) {
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
                                    } else if (it.text.contains("…thêm")) {
                                        tap(it)
                                    }
                                    endAction()
                                }
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

                            2f to {
                                pressHome()
                                rate.reset()
                                this@scene.killApp()
                                stop()
                            }
                        )
                    }
                }
            }

            screen("Unknow") {
                detect {
                    any(text("Đã hiểu"), text("Không cho phép"))
                }

                action("Click") {
                    find(text("Đã hiểu"))?.let {
                        tap(it)
                    }
                    find(text("Không cho phép"))?.let {
                        tap(it)
                    }
                }
            }
        }

        loop {

        }
    }

    @Test
    fun rePost() = runScene {
        scene("tiktok_rePost") {
            config {
                targetPackage = "com.ss.android.ugc.trill"
                onUnknownScreen = UnknownScreenPolicy.PRESS_BACK
                recoveryTimeoutMs = 15000L
            }

            handleUnknowScreen {
                if(this.context.consecutiveUnknownScreens > 8) {
                    context.stop("Failure", -2)
                }
            }


            screen("Profile") {
                detect {
                    has(id("com.ss.android.ugc.trill:id/hdm"))
                }

                action("Choose Video") {
                    find(id("com.ss.android.ugc.trill:id/hdm"))?.findObjects(id("com.ss.android.ugc.trill:id/z9y").toBySelector()).let {
                        if (it?.isEmpty() ?: true) {
                            stop("No Videos")
                        }
                        for (i in 0..(it?.size ?: 0)) {
                            val videoText = it!![i]
                            if (videoText.text.trim().toInt() < 2) {
                                tap(videoText)
                                wait(random(1000, 3000))
                                return@action
                            }
                            if (i >= (it.size - 1)) {
                                stop("Không tìm thấy video 0 View nào")
                            }
                        }
                        endAction()
                    }
                }
            }

            screen("Video Search") {
                detect { has(text("Tìm kiếm") and id("com.ss.android.ugc.trill:id/user_avatar")) }

                action("Lướt xem Video Search") {
                    find(id("com.ss.android.ugc.trill:id/ubv"))?.let {
                        tap(it)
                        wait(random(1000, 1500))
                        endAction()
                    }
                }
            }

            screen("Share Screen") {
                detect {
                    has(id("com.ss.android.ugc.trill:id/znd"))
                }
                action("Swipe to choose delete") {
                    find(id("com.ss.android.ugc.trill:id/vv"))?.scroll(Direction.RIGHT, 0.8f)
                    wait(random(1000, 1500))
                    find(text("Xóa"))?.let {
                        tap(it)
                        wait(random(1000, 1500))
                    }
                    endAction()
                }
            }

            screen("RePost Popup") {
                detect {
                    has(id("com.ss.android.ugc.trill:id/ofw"))
                }

                action("Repost") {
                    find(id("com.ss.android.ugc.trill:id/sbo")).let {
                        if (it != null) {
                            tap(it)
                        }
                        wait(random(2000, 5000))
                        endAction()
                    }
                }
            }

            screen("Delete Popup") {
                detect {
                    has(id("com.ss.android.ugc.trill:id/xd"))
                }

                action("Repost") {
                    find(id("com.ss.android.ugc.trill:id/wk"))?.let { deleteButon ->
                        tap(deleteButon)
                        wait(random(2000, 5000))
                        stop("Đã xóa video")
                    }
                }
            }

            screen("Edit video screen") {
                detect {
                    has(id("com.ss.android.ugc.trill:id/xgp"))
                }

                action("CLick continue") {
                    tap(find(id("com.ss.android.ugc.trill:id/ond")))
                    wait(random(2000, 5000))
                    endAction()
                }
            }

            screen("Post video screen") {
                detect {
                    has(id("com.ss.android.ugc.trill:id/jy1"))
                }

                action("CLick post") {
                    tap(find(text("Đăng")))
                    wait(random(20000, 40000))
                    stop("Đã đăng lại video")
                }
            }

            screen("Unknow") {
                detect {
                    any(text("Đã hiểu"), text("Không cho phép"))
                }

                action("Click") {
                    find(text("Đã hiểu"))?.let {
                        tap(it)
                    }
                    find(text("Không cho phép"))?.let {
                        tap(it)
                    }
                }
            }
        }

        loop {

        }
    }

    @Test
    fun delete0() = runScene {
        var hasDeleteVideo = false
        scene("tiktok_rePost") {
            config {
                targetPackage = "com.ss.android.ugc.trill"
                onUnknownScreen = UnknownScreenPolicy.PRESS_BACK
                recoveryTimeoutMs = 15000L
            }

            handleUnknowScreen {
                if(this.context.consecutiveUnknownScreens > 8) {
                    context.stop("Failure", -2)
                }
            }


            screen("Profile") {
                detect {
                    has(id("com.ss.android.ugc.trill:id/hdm"))
                }

                action("Choose Video") {
                    find(id("com.ss.android.ugc.trill:id/hdm"))?.findObjects(id("com.ss.android.ugc.trill:id/z9y").toBySelector()).let {
                        if (it?.isEmpty() ?: true) {
                            stop("No Videos")
                        }
                        for (i in 0..(it?.size ?: 0)) {
                            val videoText = it!![i]
                            if (videoText.text.trim().toInt() < 10) {
                                tap(videoText)
                                wait(random(1000, 3000))
                                return@action
                            }
                            if (i >= (it.size - 1)) {
                                stop(if (hasDeleteVideo) "Đã xóa tất cả video 0 View" else "Không tìm thấy video 0 View nào")
                            }
                        }
                        endAction()
                    }
                }
            }

            screen("Video Search") {
                detect { has(text("Tìm kiếm") and id("com.ss.android.ugc.trill:id/user_avatar")) }

                action("Lướt xem Video Search") {
                    find(id("com.ss.android.ugc.trill:id/ubv"))?.let {
                        tap(it)
                        wait(random(1000, 1500))
                        endAction()
                    }
                }
            }

            screen("Share Screen") {
                detect {
                    has(id("com.ss.android.ugc.trill:id/znd"))
                }
                action("Swipe to choose delete") {
                    find(id("com.ss.android.ugc.trill:id/vv"))?.scroll(Direction.RIGHT, 0.8f)
                    wait(random(1000, 1500))
                    find(text("Xóa"))?.let {
                        tap(it)
                        wait(random(1000, 1500))
                    }
                    endAction()
                }
            }

            screen("RePost Popup") {
                detect {
                    has(id("com.ss.android.ugc.trill:id/ofw"))
                }

                action("Repost") {
                    find(id("com.ss.android.ugc.trill:id/f9z"))?.let {
                        tap(it)
                        hasDeleteVideo = true
                        wait(random(2000, 5000))
                        pressBack()
                        endAction()
                    }
                }
            }

            screen("Delete Popup") {
                detect {
                    has(id("com.ss.android.ugc.trill:id/xd"))
                }

                action("Repost") {
                    find(text("Xóa"))?.let { deleteButon ->
                        tap(deleteButon)
                        hasDeleteVideo = true
                        wait(random(2000, 5000))
                        pressBack()
                        endAction()
                    }
                }
            }

            screen("Unknow") {
                detect {
                    any(text("Đã hiểu"), text("Không cho phép"))
                }

                action("Click") {
                    find(text("Đã hiểu"))?.let {
                        tap(it)
                    }
                    find(text("Không cho phép"))?.let {
                        tap(it)
                    }
                }
            }
        }

        loop {

        }
    }
}

class AutoRate(
    private val _swipeRate: Float = 4f,
    private val _likeRate: Float = 1f,
    private val _commentRate: Float = 1f,
    private val _favoriteRate: Float = 1f,
    private val _rePostRate: Float = 1f,
    private val _copyLinkRate: Float = 1f,
    private val _exitRate: Float = 1f
) {

    private var __swipeRate: Float = _swipeRate
    private var __likeRate: Float = _likeRate

    private var __commentRate: Float = _commentRate

    private var __favoriteRate: Float = _favoriteRate

    private var __rePostRate: Float = _rePostRate

    private var __copyLinkRate: Float = _copyLinkRate

    private var __exitRate: Float = _exitRate

    val swipeRate: Float
        get() = __swipeRate
    val likeRate: Float
        get() = __likeRate
    val commentRate: Float
        get() = __commentRate
    val favoriteRate: Float
        get() = __favoriteRate
    val rePostRate: Float
        get() = __rePostRate
    val copyLinkRate: Float
        get() = __copyLinkRate
    val exitRate: Float
        get() = __exitRate

    fun onSwipe() {
        if (__swipeRate < 1f) return
        __swipeRate -= 1f
    }

    fun onLike() {
        if (__likeRate < 1f) return
        __likeRate -= 1f
    }

    fun onComment() {
        if (__commentRate < 1f) return
        __commentRate -= 1f
    }

    fun onFavorite() {
        if (__favoriteRate < 1f) return
        __favoriteRate -= 1f
    }

    fun onRePost() {
        if (__rePostRate < 1f) return
        __rePostRate -= 1f
    }

    fun onCopyLink() {
        if (__copyLinkRate < 1f) return
        __copyLinkRate -= 1f
    }

    fun onExit() {
        if (__exitRate < 1f) return
        __exitRate -= 1f
    }

    fun reset() {
        __swipeRate = _swipeRate
        __likeRate = _likeRate
        __commentRate = _commentRate
        __favoriteRate = _favoriteRate
        __rePostRate = _rePostRate
        __copyLinkRate = _copyLinkRate
        __exitRate = _exitRate
    }
}
