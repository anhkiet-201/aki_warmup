package com.aki.akiwarmup

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aki.akiwarmup.core.dsl.runScene
import com.aki.akiwarmup.scenes.TikTokWarmupScene
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AkiFrameworkTest {

    @Test
    fun warmUp() = runScene(TikTokWarmupScene) { scene ->
        // Khởi chạy ứng dụng (tự động lấy package từ scene)
        launchApp()

        scene.loop {}
    }
}
