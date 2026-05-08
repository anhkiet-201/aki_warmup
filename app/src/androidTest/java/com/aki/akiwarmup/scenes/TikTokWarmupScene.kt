package com.aki.akiwarmup.scenes

import com.aki.akiwarmup.core.dsl.UnknownScreenPolicy
import com.aki.akiwarmup.core.dsl.clazz
import com.aki.akiwarmup.core.dsl.id
import com.aki.akiwarmup.core.dsl.scene

val TikTokWarmupScene = scene("tiktok_warmup") {
    config {
        targetPackage = "com.ss.android.ugc.trill"
        onUnknownScreen = UnknownScreenPolicy.PRESS_BACK
        recoveryTimeoutMs = 15000L
    }

    this.sceneConfig

    val keyWorlds = listOf<String>(
        "ttnhr",
        "Việc làm bình dương",
        "Việc làm thời vụ Bình dương",
        "Tìm việc Vsip 2A",
        "Việc làm ở Mỹ Phước",
        "tìm việc ttnhr"
    )
    // Nếu không nhận dạng ra screen nào thì chạy vào hàm này
    handleUnknowScreen {
        // Nếu restart 3 lần liên tiếp mà không thực hiện bất kỳ screen nào
        if(restartCount > 3) {
            stop("Lỗi app")
        }
    }

    screen("Home") {
        detect {
            all(
               id("android:id/text1"),
                id("com.ss.android.ugc.trill:id/user_avatar")
            )
        }
        actions {
            action("Lướt xem") {
                loop {
                    wait(random(500, 20000))
                    choose(
                        6f to {
                            swipeUp()
                        },
                        0.5f to {
                            find(id("com.ss.android.ugc.trill:id/fhc"))?.let {
                                tap(it)
                                wait(random(min = 1000, max = 3000))
                            }
                        },
                        0.5f to {
                            find(id("com.ss.android.ugc.trill:id/h_9"))?.let {
                                tap(it)
                                wait(random(min = 1000, max = 3000))
                            }
                        },
                        3f to {
                            find("com.ss.android.ugc.trill:id/jb1")?.let {
                                tap(it)
                                wait(300)
                                endAction()
                            }
                        }
                    )
                }
            }
        }
    }

    screen("Search") {
        detect {
            has(id("com.ss.android.ugc.trill:id/gz8") and id("com.ss.android.ugc.trill:id/voice_button_click_area"))
        }

        actions {
            action("Nhập từ khóa") {
                find(id("com.ss.android.ugc.trill:id/gz8"))?.let {
                    tap(it)
                    wait(500)
                    humanType(it, keyWorlds.random())
                    wait(random(200))
                }
                find(id("com.ss.android.ugc.trill:id/trq"))?.let {
                    tap(it)
                    wait(random(1000))
                }
            }
        }
    }

    screen("Search Result") {
        detect { has(id("com.ss.android.ugc.trill:id/viewpager_search")) }
        actions {
            action("sd") {
                sometimes(0.5f) {
                    swipeUp()
                }
                find(id("com.ss.android.ugc.trill:id/m_7"))?.findObjects(clazz("android.view.View").toBySelector()).run {
                    this?.take(4)?.let {
                        tap(it.random())
                    }
                }
            }
        }
    }

    screen("Video Search") {
        detect { has(id("com.ss.android.ugc.trill:id/e02")) }

        actions {
            action("Lướt xem") {
                loop {
                    wait(random(1000, 30000))
                    choose(
                        7f to {
                            swipeUp()
                        },
                        0.5f to {
                            find(id("com.ss.android.ugc.trill:id/fhc"))?.let {
                                tap(it)
                                wait(random(min = 1000, max = 3000))
                            }
                        },
                        0.5f to {
                            find(id("com.ss.android.ugc.trill:id/h_9"))?.let {
                                tap(it)
                                wait(random(min = 1000, max = 3000))
                            }
                        },
                        2f to {
                            pressHome()
                            stop()
                        }
                    )
                }
            }
        }
    }

    // --- SCREEN 1: For You Page ---
//    screen("for_you_page") {
//        detect {
//            any(
//                id("com.zhiliaoapp.musically:id/feed_video_view"),
//                desc("For You"),
//                text("For you")
//            )
//        }
//
//        actions {
//            action("watch_video", weight = 50) {
//                val watchTime = (5..45).random().toLong() * 1000
//                wait(watchTime)
//                sometimes(0.3f) {
//                    microScroll()
//                }
//            }
//
//            action("swipe_next", weight = 30) {
//                swipeUp(humanized = true)
//                wait(800)
//            }
//
//            action("like_video", weight = 10) {
//                // Double tap in the center of the screen
//                val width = device.displayWidth
//                val height = device.displayHeight
//                doubleTap(android.graphics.Point(width / 2, height / 2))
//                wait(1000)
//            }
//
//            action("go_to_profile", weight = 5) {
//                tap(find(id("com.zhiliaoapp.musically:id/iv_author")), humanized = true)
//                // Navigation will be detected by the loop in the next iteration
//                wait(2000)
//            }
//
//            action("open_search", weight = 3) {
//                tap(find(id("com.zhiliaoapp.musically:id/tab_search")), humanized = true)
//                wait(2000)
//            }
//        }
//    }
//
//    // --- SCREEN 2: Creator Profile ---
//    screen("creator_profile") {
//        detect {
//            has(id("com.zhiliaoapp.musically:id/profile_root"))
//        }
//
//        actions {
//            action("follow", weight = 60) {
//                val btn = find(id("com.zhiliaoapp.musically:id/btn_follow"))
//                if (btn?.text != "Following") {
//                    tap(btn, humanized = true)
//                }
//                wait(1500)
//                pressBack()
//            }
//
//            action("browse_profile", weight = 40) {
//                scroll(ScrollDirection.Down, (300..800).random())
//                wait(2000)
//                pressBack()
//            }
//        }
//    }
//
//    // --- SCREEN 3: Search Page ---
//    screen("search_page") {
//        detect {
//            has(id("com.zhiliaoapp.musically:id/search_root"))
//        }
//
//        actions {
//            action("search_keyword", weight = 100) {
//                val field = find(id("com.zhiliaoapp.musically:id/et_search_kw"))
//                humanType(field, "tiktok warmup")
//                pressEnter()
//                wait(10000) // Watch results for 10s
//                pressBack()
//                wait(500)
//                pressBack()
//            }
//        }
//    }
}
