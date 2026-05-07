package com.aki.akiwarmup.scenes

import com.aki.akiwarmup.core.dsl.UnknownScreenPolicy
import com.aki.akiwarmup.core.dsl.desc
import com.aki.akiwarmup.core.dsl.id
import com.aki.akiwarmup.core.dsl.scene
import com.aki.akiwarmup.core.dsl.text
import java.util.Random

val TikTokWarmupScene = scene("tiktok_warmup") {
    
    config {
        targetPackage = "com.zhiliaoapp.musically"
        onUnknownScreen = UnknownScreenPolicy.PRESS_BACK
        recoveryTimeoutMs = 15000L
    }

    // --- SCREEN 1: For You Page ---
    screen("for_you_page") {
        detect {
            any(
                id("com.zhiliaoapp.musically:id/feed_video_view"),
                desc("For You"),
                text("For you")
            )
        }
        
        actions {
            action("watch_video", weight = 50) {
                val watchTime = (5..45).random().toLong() * 1000
                wait(watchTime)
                sometimes(0.3f) {
                    microScroll()
                }
            }
            
            action("swipe_next", weight = 30) {
                swipeUp(humanized = true)
                wait(800)
            }
            
            action("like_video", weight = 10) {
                // Double tap in the center of the screen
                val width = device.displayWidth
                val height = device.displayHeight
                doubleTap(android.graphics.Point(width / 2, height / 2))
                wait(1000)
            }
            
            action("go_to_profile", weight = 5) {
                tap(find(id("com.zhiliaoapp.musically:id/iv_author")), humanized = true)
                // Navigation will be detected by the loop in the next iteration
                wait(2000)
            }
            
            action("open_search", weight = 3) {
                tap(find(id("com.zhiliaoapp.musically:id/tab_search")), humanized = true)
                wait(2000)
            }
        }
    }

    // --- SCREEN 2: Creator Profile ---
    screen("creator_profile") {
        detect {
            has(id("com.zhiliaoapp.musically:id/profile_root"))
        }
        
        actions {
            action("follow", weight = 60) {
                val btn = find(id("com.zhiliaoapp.musically:id/btn_follow"))
                if (btn?.text != "Following") {
                    tap(btn, humanized = true)
                }
                wait(1500)
                pressBack()
            }
            
            action("browse_profile", weight = 40) {
                scroll("DOWN", (300..800).random())
                wait(2000)
                pressBack()
            }
        }
    }

    // --- SCREEN 3: Search Page ---
    screen("search_page") {
        detect {
            has(id("com.zhiliaoapp.musically:id/search_root"))
        }
        
        actions {
            action("search_keyword", weight = 100) {
                val field = find(id("com.zhiliaoapp.musically:id/et_search_kw"))
                humanType(field, "tiktok warmup")
                pressEnter()
                wait(10000) // Watch results for 10s
                pressBack()
                wait(500)
                pressBack()
                scroll()
            }
        }
    }
}
