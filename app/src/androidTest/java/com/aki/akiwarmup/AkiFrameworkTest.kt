package com.aki.akiwarmup

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aki.akiwarmup.core.dsl.runScene
import com.aki.akiwarmup.tiktok.action.keyWorlds
import com.aki.akiwarmup.tiktok.action.onChooseVideo
import com.aki.akiwarmup.tiktok.action.onTiktokSharePostAction
import com.aki.akiwarmup.tiktok.action.onUnknowViewAction
import com.aki.akiwarmup.tiktok.action.openVideoMenu
import com.aki.akiwarmup.tiktok.action.selectRandomMusic
import com.aki.akiwarmup.tiktok.action.selectUser
import com.aki.akiwarmup.tiktok.action.selectVideoAfterSearch
import com.aki.akiwarmup.tiktok.action.swipeToChooseDelete
import com.aki.akiwarmup.tiktok.action.tapDeleteAndRepost
import com.aki.akiwarmup.tiktok.action.tapDeleteInRepostPopup
import com.aki.akiwarmup.tiktok.action.tapDeleteVideo
import com.aki.akiwarmup.tiktok.action.tapToAddMusic
import com.aki.akiwarmup.tiktok.action.tapToUpload
import com.aki.akiwarmup.tiktok.action.typeCaption
import com.aki.akiwarmup.tiktok.action.typeSearchKeyword
import com.aki.akiwarmup.tiktok.action.watchVideo
import com.aki.akiwarmup.tiktok.model.AutoRate
import com.aki.akiwarmup.tiktok.model.RateType
import com.aki.akiwarmup.tiktok.scene.tiktokSceneDefine
import com.aki.akiwarmup.tiktok.screen.onAddInfoView
import com.aki.akiwarmup.tiktok.screen.onDeletePopup
import com.aki.akiwarmup.tiktok.screen.onHome
import com.aki.akiwarmup.tiktok.screen.onProfile
import com.aki.akiwarmup.tiktok.screen.onRepostPopup
import com.aki.akiwarmup.tiktok.screen.onSearch
import com.aki.akiwarmup.tiktok.screen.onSearchResult
import com.aki.akiwarmup.tiktok.screen.onSelectMusicSheet
import com.aki.akiwarmup.tiktok.screen.onShare
import com.aki.akiwarmup.tiktok.screen.onTiktokSharePost
import com.aki.akiwarmup.tiktok.screen.onUnknowView
import com.aki.akiwarmup.tiktok.screen.onVideoPreview
import com.aki.akiwarmup.tiktok.screen.onVideoView
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
                    if (this.context.restartCount > 3) {
                        this.context.stop("lỖI APP")
                    }
                }

                launchApp()

                screen {
                    onHome(context) {
                        action {
                            watchVideo(context) {
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
                            watchVideo(context) {
                                pressHome()
                                this@tiktokSceneDefine.killApp()
                                stop("Hoàn thành")
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
                    if (this.context.consecutiveUnknownScreens > 8) {
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
        val rate = AutoRate(mapOf(
            RateType.SWIPE to 5, RateType.LIKE to 3,
            RateType.FAVORITE to 3, RateType.COMMENT to 2,
            RateType.EXIT to 2
        ))

        scene {
            tiktokSceneDefine("Seeding", context) {
                handleUnknowScreen {
                    Log.i("AkiFramework", "${context.consecutiveUnknownScreens}")
                    if (this.context.consecutiveUnknownScreens > 8) {
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
                    if (this.context.consecutiveUnknownScreens > 8) {
                        context.stop("Failure", -2)
                    }
                }

                screen {
                    onProfile(context) {
                        action {
                            onChooseVideo(context, {
                                stop("No Videos")
                            }) { videos ->
                                // Fix: dùng withIndex() thay vì 0..(videos.size) để tránh IndexOutOfBounds
                                for ((i, videoText) in videos.withIndex()) {
                                    if ((videoText.text.trim().toIntOrNull() ?: Int.MAX_VALUE) < 10) {
                                        tap(videoText)
                                        wait(random(1000, 3000))
                                        return@onChooseVideo
                                    }
                                    if (i >= videos.size - 1) {
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
                    if (this.context.consecutiveUnknownScreens > 8) {
                        context.stop("Failure", -2)
                    }
                }

                screen {
                    onProfile(context) {
                        action {
                            onChooseVideo(context, {
                                stop("No Videos")
                            }) { videos ->
                                // Fix: dùng withIndex() thay vì 0..(videos.size) để tránh IndexOutOfBounds
                                for ((i, videoText) in videos.withIndex()) {
                                    if ((videoText.text.trim().toIntOrNull() ?: Int.MAX_VALUE) < 10) {
                                        tap(videoText)
                                        wait(random(1000, 3000))
                                        return@onChooseVideo
                                    }
                                    if (i >= videos.size - 1) {
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
