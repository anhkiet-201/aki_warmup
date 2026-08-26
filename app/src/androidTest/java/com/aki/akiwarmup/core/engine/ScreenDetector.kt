package com.aki.akiwarmup.core.engine

import com.aki.akiwarmup.core.logger.AkiLog
import com.aki.akiwarmup.core.logger.LogTag
import androidx.test.uiautomator.UiDevice
import com.aki.akiwarmup.core.dsl.ScreenDef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

import androidx.test.uiautomator.Configurator

class ScreenDetector(
    private val device: UiDevice,
    screens: List<ScreenDef>,
    private val detectTimeoutMs: Long = 700L,
    private val actionTimeoutMs: Long = 3000L
) {
    // Sắp xếp các màn hình theo priority giảm dần (cao nhất đứng trước)
    private val sortedScreens = screens.sortedByDescending { it.priority }

    /**
     * Nhận diện màn hình hiện tại bằng cách kiểm tra SONG SONG (Parallel).
     * Áp dụng cơ chế Early-exit: trả về ngay lập tức khi coroutine đầu tiên tìm thấy match
     * và hủy bỏ (cancel) tất cả các truy vấn UIAutomator của các screen còn lại.
     */
    suspend fun detectCurrent(): ScreenDef? = coroutineScope {
        val start = System.currentTimeMillis()
        AkiLog.v(LogTag.ENGINE, "detecting (${sortedScreens.size} screens in parallel)")

        val configurator = Configurator.getInstance()

        try {
            // Ép timeout ngắn cho detect phase
            configurator.waitForSelectorTimeout = detectTimeoutMs

            // Dùng Channel có buffer bằng số lượng screen để chứa kết quả
            val channel = kotlinx.coroutines.channels.Channel<ScreenDef?>(sortedScreens.size)

            val jobs = sortedScreens.map { screen ->
                launch(Dispatchers.IO) {
                    val screenStart = System.currentTimeMillis()
                    val detected = screen.detectPredicate.evaluate(device)
                    val duration = System.currentTimeMillis() - screenStart

                    if (detected) {
                        AkiLog.i(LogTag.ENGINE, "match [${screen.id}] (${duration}ms)")
                        channel.send(screen)
                    } else {
                        channel.send(null)
                    }
                }
            }

            var result: ScreenDef? = null
            
            // Lắng nghe kết quả trả về từ các coroutine
            for (i in 0 until sortedScreens.size) {
                val screen = channel.receive()
                if (screen != null) {
                    // Đã tìm thấy match đầu tiên!
                    result = screen
                    // Hủy tất cả các jobs đang chạy dở để tiết kiệm tài nguyên
                    jobs.forEach { it.cancel() }
                    break
                }
            }

            channel.close()

            val totalDuration = System.currentTimeMillis() - start
            if (result == null) {
                AkiLog.w(LogTag.ENGINE, "No screen matched (${totalDuration}ms)")
            }

            return@coroutineScope result

        } finally {
            // Phục hồi timeout dài cho action phase
            configurator.waitForSelectorTimeout = actionTimeoutMs
        }
    }
}
