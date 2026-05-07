package com.aki.akiwarmup.core.loop

import androidx.test.uiautomator.UiDevice
import com.aki.akiwarmup.core.config.RunConfig
import com.aki.akiwarmup.core.dsl.ActionDef
import com.aki.akiwarmup.core.dsl.ActionExecutionContext
import com.aki.akiwarmup.core.dsl.Scene
import com.aki.akiwarmup.core.dsl.UnknownScreenPolicy
import com.aki.akiwarmup.core.human.HumanBehaviorEngine
import com.aki.akiwarmup.core.logger.SessionLogger
import com.aki.akiwarmup.core.screen.ScreenDetector
import kotlinx.coroutines.delay
import java.util.Random

class ActionLoop(
    private val device: UiDevice,
    private val scene: Scene,
    private val detector: ScreenDetector,
    private val humanEngine: HumanBehaviorEngine,
    private val logger: SessionLogger,
    private val runConfig: RunConfig
) {
    private val random = Random()
    private var isStopped = false

    fun stop() {
        isStopped = true
    }

    suspend fun run(iterations: Int = -1, onIterationComplete: suspend () -> Unit = {}) {
        isStopped = false
        val endTime = if (runConfig.durationMs > 0) System.currentTimeMillis() + runConfig.durationMs else Long.MAX_VALUE
        var consecutiveUnknownScreens = 0
        var currentIteration = 0
        
        while (!isStopped && (iterations == -1 || currentIteration < iterations) && System.currentTimeMillis() < endTime) {
            val currentScreen = detector.detectCurrent()
            
            if (currentScreen == null) {
                consecutiveUnknownScreens++
                handleUnknownScreen(consecutiveUnknownScreens)
                continue
            }
            
            consecutiveUnknownScreens = 0
            val action = selectWeightedAction(currentScreen.actions)
            
            val result = runCatching {
                val ctx = ActionExecutionContext(device, humanEngine, scene.config, runConfig)
                action.block.invoke(ctx)
            }
            
            logger.log(currentScreen.id, action.id, result)
            humanEngine.breathingPause()
            
            onIterationComplete()
            currentIteration++
        }
    }
    
    private fun selectWeightedAction(actions: List<ActionDef>): ActionDef {
        if (actions.isEmpty()) throw IllegalStateException("No actions defined for screen")
        val totalWeight = actions.sumOf { it.weight }
        var rand = random.nextInt(totalWeight)
        return actions.first { 
            rand -= it.weight
            rand < 0 
        }
    }
    
    private suspend fun handleUnknownScreen(count: Int) {
        when {
            count < 3 -> {
                humanEngine.gaussianDelay(1000, 200)
            }
            count < 6 -> {
                val policy = scene.config.onUnknownScreen
                when (policy) {
                    UnknownScreenPolicy.PRESS_BACK -> device.pressBack()
                    UnknownScreenPolicy.WAIT -> delay(2000)
                    UnknownScreenPolicy.RESTART_APP -> restartTargetApp()
                }
            }
            else -> restartTargetApp()
        }
    }

    private fun restartTargetApp() {
        val pkg = scene.config.targetPackage
        if (pkg.isNotEmpty()) {
            device.executeShellCommand("am force-stop $pkg")
            delaySync(1000)
            device.executeShellCommand("monkey -p $pkg -c android.intent.category.LAUNCHER 1")
            delaySync(3000)
        }
    }
    
    private fun delaySync(ms: Long) {
        try { Thread.sleep(ms) } catch (e: Exception) {}
    }
}
