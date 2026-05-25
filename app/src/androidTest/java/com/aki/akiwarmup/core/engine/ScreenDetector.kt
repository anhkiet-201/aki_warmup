package com.aki.akiwarmup.core.engine

import android.util.Log
import androidx.test.uiautomator.UiDevice
import com.aki.akiwarmup.core.dsl.ScreenDef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class ScreenDetector(
    private val device: UiDevice,
    private val screens: List<ScreenDef>
) {
    companion object {
        private const val TAG = "ScreenDetector"
    }

    /**
     * Nhận diện màn hình hiện tại bằng cách kiểm tra song song tất cả screens.
     * Sử dụng coroutines để giảm thời gian detect từ O(n * t) xuống O(t) trong trường hợp lý tưởng.
     *
     * Lưu ý: UIAutomator không hoàn toàn thread-safe. Nếu phát sinh race condition
     * trên thiết bị cụ thể, cân nhắc fallback về sequential detection.
     */
    suspend fun detectCurrent(): ScreenDef? = coroutineScope {
        val start = System.currentTimeMillis()
        Log.d(TAG, "--- Start Screen Detection (${screens.size} screens defined) ---")

        val jobs = screens.map { screen ->
            async(Dispatchers.IO) {
                val screenStart = System.currentTimeMillis()
                val detected = screen.detectPredicate.evaluate(device)
                val duration = System.currentTimeMillis() - screenStart

                if (detected) {
                    Log.i(TAG, "SUCCESS: Detected [${screen.id}] in ${duration}ms")
                    screen
                } else {
                    Log.v(TAG, "Checking [${screen.id}]: Not matched (${duration}ms)")
                    null
                }
            }
        }

        val result = jobs.map { it.await() }.filterNotNull().firstOrNull()
        val totalDuration = System.currentTimeMillis() - start

        if (result == null) {
            Log.w(TAG, "FAILED: No screen detected! (Total time: ${totalDuration}ms)")
        }

        result
    }
}
