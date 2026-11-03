package com.aki.akiwarmup.core.engine

import androidx.test.uiautomator.Configurator
import androidx.test.uiautomator.UiDevice
import com.aki.akiwarmup.core.dsl.ScreenDef
import com.aki.akiwarmup.core.logger.AkiLog
import com.aki.akiwarmup.core.logger.LogTag
import kotlinx.coroutines.delay

class ScreenDetector(
    private val device: UiDevice,
    screens: List<ScreenDef>,
    private val detectTimeoutMs: Long = 700L,
    private val actionTimeoutMs: Long = 3000L
) {
    // Sắp xếp các màn hình theo priority giảm dần (cao nhất đứng trước)
    private val sortedScreens = screens.sortedByDescending { it.priority }

    /**
     * Nhận diện màn hình hiện tại theo thứ tự ưu tiên (Priority-Ordered Fast Snapshot).
     * Đặt waitForSelectorTimeout = 0L trong pha detect để mỗi truy vấn UIAutomator kiểm tra tức thời
     * cây Accessibility hiện tại mà không bị block chờ khi không khớp (đặc biệt với NotSelector),
     * đồng thời đảm bảo trả về ngay lập tức màn hình có priority cao nhất khớp đầu tiên.
     */
    suspend fun detectCurrent(): ScreenDef? {
        if (sortedScreens.isEmpty()) return null
        val start = System.currentTimeMillis()
        AkiLog.v(LogTag.ENGINE, "detecting (${sortedScreens.size} screens by priority)")

        val configurator = Configurator.getInstance()

        try {
            // Ép timeout = 0L để kiểm tra snapshot tức thời cho detect phase
            configurator.waitForSelectorTimeout = 0L

            while (true) {
                for (screen in sortedScreens) {
                    val screenStart = System.currentTimeMillis()
                    val detected = screen.detectPredicate.evaluate(device)
                    if (detected) {
                        val duration = System.currentTimeMillis() - screenStart
                        AkiLog.i(LogTag.ENGINE, "match [${screen.id}] (${duration}ms)")
                        return screen
                    }
                }

                val elapsed = System.currentTimeMillis() - start
                if (elapsed >= detectTimeoutMs) {
                    AkiLog.w(LogTag.ENGINE, "No screen matched (${elapsed}ms)")
                    return null
                }

                val remaining = (detectTimeoutMs - elapsed).coerceAtMost(100L)
                delay(remaining)
            }
        } finally {
            // Phục hồi timeout dài cho action phase
            configurator.waitForSelectorTimeout = actionTimeoutMs
        }
    }
}
