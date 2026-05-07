package com.aki.akiwarmup.core.screen

import androidx.test.uiautomator.UiDevice
import com.aki.akiwarmup.core.dsl.ScreenDef

class ScreenDetector(
    private val device: UiDevice,
    private val screens: List<ScreenDef>
) {
    fun detectCurrent(): ScreenDef? {
        return screens.firstOrNull { screen ->
            screen.detectPredicate.evaluate(device)
        }
    }
}
