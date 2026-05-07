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

        // Vòng lặp vô hạn (chạy theo cấu trúc của scene)
        scene.loop {
            // Logic xử lý sau mỗi bước lặp (nếu cần)
            // Ví dụ: if (someCondition) stop()
        }

        // Vòng lặp với số lần cụ thể (ví dụ: 5 lần)
        scene.loop(5) {
            // Thực hiện sau mỗi bước

        }
        // Chạy duy nhất 1 lần
        scene.run {
            // Thực hiện sau bước chạy
        }
    }
}
