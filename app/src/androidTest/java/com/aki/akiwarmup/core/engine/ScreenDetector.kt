package com.aki.akiwarmup.core.engine

import com.aki.akiwarmup.core.logger.AkiLog
import com.aki.akiwarmup.core.logger.LogTag
import androidx.test.uiautomator.UiDevice
import com.aki.akiwarmup.core.dsl.ScreenDef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

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
     * Nhận diện màn hình hiện tại bằng cách kiểm tra tuần tự.
     * Áp dụng cơ chế Early-exit: trả về ngay lập tức khi tìm thấy match đầu tiên.
     * Sử dụng detectTimeoutMs ngắn để tránh chờ lâu khi không có match.
     */
    suspend fun detectCurrent(): ScreenDef? {
        val start = System.currentTimeMillis()
        AkiLog.v(LogTag.ENGINE, "detecting (${sortedScreens.size} screens)")

        val configurator = Configurator.getInstance()
        val originalTimeout = configurator.waitForSelectorTimeout

        try {
            // Ép timeout ngắn cho detect phase
            configurator.waitForSelectorTimeout = detectTimeoutMs

            for (screen in sortedScreens) {
                val screenStart = System.currentTimeMillis()
                val detected = screen.detectPredicate.evaluate(device)
                val duration = System.currentTimeMillis() - screenStart

                if (detected) {
                    AkiLog.v(LogTag.ENGINE, "match [${screen.id}] (${duration}ms)")
                    return screen
                } else {
                    AkiLog.v(LogTag.ENGINE, "miss  [${screen.id}] (${duration}ms)")
                }
            }
        } finally {
            // Phục hồi timeout dài cho action phase
            configurator.waitForSelectorTimeout = actionTimeoutMs
        }

        val totalDuration = System.currentTimeMillis() - start
        AkiLog.w(LogTag.ENGINE, "No screen matched (${totalDuration}ms)")

        return null
    }
}
