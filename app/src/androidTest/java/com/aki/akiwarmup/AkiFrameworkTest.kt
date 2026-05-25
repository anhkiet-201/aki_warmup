package com.aki.akiwarmup

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.type
import androidx.test.uiautomator.uiAutomator
import com.aki.akiwarmup.core.dsl.ActionBuilder
import com.aki.akiwarmup.core.dsl.SceneExecutionContext
import com.aki.akiwarmup.core.dsl.UnknownScreenPolicy
import com.aki.akiwarmup.core.dsl.clazz
import com.aki.akiwarmup.core.dsl.defineAction
import com.aki.akiwarmup.core.dsl.desc
import com.aki.akiwarmup.core.dsl.id
import com.aki.akiwarmup.core.dsl.runScene
import com.aki.akiwarmup.core.dsl.text
import com.aki.akiwarmup.core.dsl.textContains
import com.aki.akiwarmup.onChooseVideo
import com.aki.akiwarmup.random.generateComment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AkiFrameworkTest {

    @Test
    fun warmUp() = runScene {
        scene {
            tiktokSceneDefine("WarmUp", context) {
                handleUnknowScreen {
                    Log.i("AkiFramework", "${context.restartCount}")
                    if(this.context.restartCount > 3) {
                        this.context.stop("lỖI APP")
                    }
                }

                launchApp()

                screen {
                    onHome(context) {
                        action {
                            watchVideo(context, AutoRate(_swipeRate = 8, _likeRate = 2, _favoriteRate = 2)) {
                                find("com.ss.android.ugc.trill:id/jb1")?.let {
                                    tap(it)
                                    wait(300)
                                }
                            }
                        }
                    }
                }

                screen {
                    onSearch(context) {
                        action {
                            typeSearchKeyword(context, keyWorlds.random())
                        }
                    }
                }

                screen {
                    onSearchResult(context) {
                        action {
                            selectVideoAfterSearch(context)
                        }
                    }
                }

                screen {
                    onVideoView(context) {
                        action {
                            watchVideo(context, AutoRate(_swipeRate = 8, _likeRate = 2, _favoriteRate = 2)) {
                                pressHome()
                                this@tiktokSceneDefine.killApp()
                            }
                        }
                    }
                }

                screen {
                    onUnknowView(context) {
                        action {
                            onUnknowViewAction(context)
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

        scene {
            tiktokSceneDefine("Auto Post", context) {
                handleUnknowScreen {
                    if(this.context.consecutiveUnknownScreens > 8) {
                        context.stop("Failure", -2)
                    }
                }

                screen {
                    onTiktokSharePost(context) {
                        action {
                            onTiktokSharePostAction(context)
                        }
                    }
                }

                screen {
                    onVideoPreview(context) {
                        action {
                            tapToAddMusic(context)
                        }
                    }
                }

                screen {
                    onSelectMusicSheet(context) {
                        action {
                            selectRandomMusic(context)
                        }
                    }
                }

                screen {
                    onAddInfoView(context) {
                        action {
                            typeCaption(context)
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
        val rate = AutoRate(_swipeRate = 5, _likeRate = 3, _favoriteRate = 3, _commentRate = 2)

        scene {
            tiktokSceneDefine("Seeding", context) {
                handleUnknowScreen {
                    Log.i("AkiFramework", "${context.consecutiveUnknownScreens}")
                    if(this.context.consecutiveUnknownScreens > 8) {
                        context.device.pressHome()
                        this.context.stop("lỖI APP")
                    }
                }

                val keyword = context.args.getString("keyword")
                if (keyword == null) {
                    context.stop("Wrong Keyword")
                }

                launchApp()

                screen {
                    onHome(context) {
                        action {
                            watchVideo(context, rate) {
                                find("com.ss.android.ugc.trill:id/jb1")?.let {
                                    tap(it)
                                    wait(300)
                                }
                            }
                        }
                    }
                }

                screen {
                    onSearch(context) {
                        action {
                            typeSearchKeyword(context, keyword!!)
                        }
                    }
                }

                screen {
                    onProfile(context) {
                        action {
                            onChooseVideo(context, {
                                stop("No Videos")
                            }) { videos ->
                                tap(videos.first())
                            }
                        }
                    }
                }

                screen {
                    onSearchResult(context) {
                        action {
                            selectUser(context, keyword!!) {
                                pressHome()
                                stop("Không tìm thấy User")
                            }
                        }
                    }
                }

                screen {
                    onVideoView(context) {
                        action {
                            watchVideo(context, rate) {
                                pressHome()
                                stop("Hoàn thành seeding: $keyword")
                            }
                        }
                    }
                }

                screen {
                    onUnknowView(context) {
                        action {
                            onUnknowViewAction(context)
                        }
                    }
                }

            }
        }

        loop {

        }
    }

    @Test
    fun rePost() = runScene {

        scene {
            tiktokSceneDefine("Tiktok repost", context) {
                handleUnknowScreen {
                    if(this.context.consecutiveUnknownScreens > 8) {
                        context.stop("Failure", -2)
                    }
                }

                screen {
                    onProfile(context) {
                        action {
                            onChooseVideo(context, {
                                stop("No Videos")
                            }) { videos ->
                                for (i in 0..(videos.size ?: 0)) {
                                    val videoText = videos[i]
                                    if (videoText.text.trim().toInt() < 10) {
                                        tap(videoText)
                                        wait(random(1000, 3000))
                                        return@onChooseVideo
                                    }
                                    if (i >= (videos.size - 1)) {
                                        stop("Không tìm thấy video 0 View nào")
                                    }
                                }
                            }
                        }
                    }
                }

                screen {
                    onVideoView(context) {
                        action {
                            openVideoMenu(context)
                        }
                    }
                }

                screen {
                    onShare(context) {
                        action {
                            swipeToChooseDelete(context)
                        }
                    }
                }

                screen {
                    onRepostPopup(context) {
                        action {
                            tapDeleteAndRepost(context)
                        }
                    }
                }

                screen {
                    onDeletePopup(context) {
                        action {
                            tapDeleteVideo(context) {
                                pressHome()
                                stop("Đã xóa video")
                            }
                        }
                    }
                }

                screen {
                    onVideoPreview(context) {
                        action {
                            tapToAddMusic(context)
                        }
                    }
                }

                screen {
                    onAddInfoView(context) {
                        action {
                            tapToUpload(context) {
                                wait(random(20000, 40000))
                                stop("Đã đăng lại video")
                            }
                        }
                    }
                }

                screen {
                    onUnknowView(context) {
                        action {
                            onUnknowViewAction(context)
                        }
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

        scene {
            tiktokSceneDefine("Delete zero view video", context) {
                handleUnknowScreen {
                    if(this.context.consecutiveUnknownScreens > 8) {
                        context.stop("Failure", -2)
                    }
                }

                screen {
                    onProfile(context) {
                        action {
                            onChooseVideo(context, {
                                stop("No Videos")
                            }) { videos ->
                                for (i in 0..(videos.size ?: 0)) {
                                    val videoText = videos[i]
                                    if (videoText.text.trim().toInt() < 10) {
                                        tap(videoText)
                                        wait(random(1000, 3000))
                                        return@onChooseVideo
                                    }
                                    if (i >= (videos.size - 1)) {
                                        stop(if (hasDeleteVideo) "Đã xóa tất cả video 0 View" else "Không tìm thấy video 0 View nào")
                                    }
                                }
                            }
                        }
                    }
                }

                screen {
                    onVideoView(context) {
                        action {
                            openVideoMenu(context)
                        }
                    }
                }

                screen {
                    onShare(context) {
                        action {
                            swipeToChooseDelete(context)
                        }
                    }
                }

                screen {
                    onRepostPopup(context) {
                        action {
                            tapDeleteInRepostPopup(context) {
                                hasDeleteVideo = true
                                wait(random(2000, 5000))
                                pressBack()
                            }
                        }
                    }
                }

                screen {
                    onDeletePopup(context) {
                        action {
                            tapDeleteVideo(context) {
                                hasDeleteVideo = true
                                wait(random(2000, 5000))
                                pressBack()
                            }
                        }
                    }
                }

                screen {
                    onUnknowView(context) {
                        action {
                            onUnknowViewAction(context)
                        }
                    }
                }
            }
        }

        loop {

        }
    }
}

class AutoRate(
    private val _swipeRate: Int = 4,
    private val _likeRate: Int = 1,
    private val _commentRate: Int = 1,
    private val _favoriteRate: Int = 1,
    private val _rePostRate: Int = 1,
    private val _copyLinkRate: Int = 1,
    private val _exitRate: Int = 1,
    private val step: Int = 1
) {

    private var __swipeRate: Int = _swipeRate
    private var __likeRate: Int = _likeRate

    private var __commentRate: Int = _commentRate

    private var __favoriteRate: Int = _favoriteRate

    private var __rePostRate: Int = _rePostRate

    private var __copyLinkRate: Int = _copyLinkRate

    private var __exitRate: Int = _exitRate

    val swipeRate: Int
        get() = __swipeRate
    val likeRate: Int
        get() = __likeRate
    val commentRate: Int
        get() = __commentRate
    val favoriteRate: Int
        get() = __favoriteRate
    val rePostRate: Int
        get() = __rePostRate
    val copyLinkRate: Int
        get() = __copyLinkRate
    val exitRate: Int
        get() = __exitRate

    fun onSwipe() {
        if (__swipeRate < step) return
        __swipeRate -= step
    }

    fun onLike() {
        if (__likeRate < step) return
        __likeRate -= step
    }

    fun onComment() {
        if (__commentRate < step) return
        __commentRate -= step
    }

    fun onFavorite() {
        if (__favoriteRate < step) return
        __favoriteRate -= step
    }

    fun onRePost() {
        if (__rePostRate < step) return
        __rePostRate -= step
    }

    fun onCopyLink() {
        if (__copyLinkRate < step) return
        __copyLinkRate -= step
    }

    fun onExit() {
        if (__exitRate < step) return
        __exitRate -= step
    }

    fun swipeBias() {
        __likeRate = 0
        __commentRate = 0
        __favoriteRate = 0
        __rePostRate = 0
        __copyLinkRate = 0
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
