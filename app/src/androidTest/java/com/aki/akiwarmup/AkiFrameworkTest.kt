package com.aki.akiwarmup

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aki.akiwarmup.core.dsl.ActionBuilder
import com.aki.akiwarmup.core.dsl.ActionDef
import com.aki.akiwarmup.core.dsl.UnknownScreenPolicy
import com.aki.akiwarmup.core.dsl.clazz
import com.aki.akiwarmup.core.dsl.id
import com.aki.akiwarmup.core.dsl.runScene
import com.aki.akiwarmup.core.dsl.text
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AkiFrameworkTest {
    @Test
    fun warmUp() = runScene {
        scene("tiktok_warmup") {
            config {
                targetPackage = "com.ss.android.ugc.trill"
                onUnknownScreen = UnknownScreenPolicy.PRESS_BACK
                recoveryTimeoutMs = 15000L
            }

            val keyWorlds = listOf<String>(
                "ttnhr",
                "Việc làm bình dương",
                "Việc làm thời vụ Bình dương",
                "Tìm việc Vsip 2A",
                "Việc làm ở Mỹ Phước",
                "tìm việc ttnhr"
            )

            handleUnknowScreen {
                Log.i("AkiFramework", "${context.restartCount}")
                if(this.context.restartCount > 3) {
                    this.context.stop("")
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

            screen("Search Screen") {
                detect {
                    has( id("com.ss.android.ugc.trill:id/gz8") and text("Tìm kiếm"))
                }
                action("Nhập từ khóa mặc định") {
                    find(id("com.ss.android.ugc.trill:id/gz8"))?.let {
                        tap(it)
                        wait(500)
                        humanType(it, keyWorlds.random())
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
                    sometimes(0.5f) {
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
                                this@scene.killApp()
                                stop()
                            }
                        )
                    }
                }
            }
        }

        loop {

        }
    }
}
