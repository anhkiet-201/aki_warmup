package com.aki.akiwarmup.core.screen

import android.util.Log
import androidx.test.uiautomator.UiDevice
import com.aki.akiwarmup.core.dsl.ScreenDef

class ScreenDetector(
    private val device: UiDevice,
    private val screens: List<ScreenDef>
) {
    companion object {
        private const val TAG = "ScreenDetector"
    }

    fun detectCurrent(): ScreenDef? {
        val start = System.currentTimeMillis()
        Log.d(TAG, "--- Start Screen Detection (${screens.size} screens defined) ---")
        
        for (screen in screens) {
            val screenStart = System.currentTimeMillis()
            val detected = screen.detectPredicate.evaluate(device)
            val duration = System.currentTimeMillis() - screenStart
            
            if (detected) {
                Log.i(TAG, "SUCCESS: Detected [${screen.id}] in ${duration}ms")
                return screen
            } else {
                Log.v(TAG, "Checking [${screen.id}]: Not matched (${duration}ms)")
            }
        }
        
        val totalDuration = System.currentTimeMillis() - start
        Log.w(TAG, "FAILED: No screen detected! (Total time: ${totalDuration}ms)")
        return null
    }
}
