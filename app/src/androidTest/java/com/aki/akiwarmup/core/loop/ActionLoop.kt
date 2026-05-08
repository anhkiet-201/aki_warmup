package com.aki.akiwarmup.core.loop

import android.util.Log
import androidx.test.uiautomator.UiDevice
import com.aki.akiwarmup.core.config.RunConfig
import com.aki.akiwarmup.core.dsl.ActionDef
import com.aki.akiwarmup.core.dsl.ActionExecutionContext
import com.aki.akiwarmup.core.dsl.Scene
import com.aki.akiwarmup.core.dsl.UnknownScreenPolicy
import com.aki.akiwarmup.core.dsl.SceneExecutionContext
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
    private var consecutiveRestarts = 0

    fun stop(reason: String = "") {
        isStopped = true
        if (reason.isNotEmpty()) {
            Log.d("AkiFramework", "Stopping execution. Reason: $reason")
        }
    }

    suspend fun run(iterations: Int = -1, onIterationComplete: suspend () -> Unit = {}) {
        if (scene.screens.isEmpty()) return
        isStopped = false
        val endTime = if (runConfig.durationMs > 0) System.currentTimeMillis() + runConfig.durationMs else Long.MAX_VALUE
        var consecutiveUnknownScreens = 0
        var currentIteration = 0
        
        while (!isStopped && (iterations == -1 || currentIteration < iterations) && System.currentTimeMillis() < endTime) {
            val currentScreen = detector.detectCurrent()
            
            if (currentScreen == null) {
                consecutiveUnknownScreens++
                
                if (scene.unknownScreenHandler != null) {
                    val androidContext = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
                    val ctx = SceneExecutionContext(androidContext, device, scene, consecutiveRestarts, { reason -> this.stop(reason) })
                    scene.unknownScreenHandler.invoke(ctx)
                } else {
                    handleUnknownScreen(consecutiveUnknownScreens)
                }
                
                // Nếu không có màn hình nào được định nghĩa, hoặc bị kẹt quá lâu, hãy dừng lại để tránh loop vô tận
                if (consecutiveUnknownScreens > 20 && scene.screens.isEmpty()) {
                    stop("No screens defined and stuck too long")
                }
                continue
            }
            
            consecutiveUnknownScreens = 0
            consecutiveRestarts = 0
            val action = selectWeightedAction(currentScreen.actions)
            
            val result = runCatching {
                val androidContext = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
                val ctx = ActionExecutionContext(androidContext, device, scene, consecutiveRestarts, currentScreen, action, humanEngine, runConfig, { reason -> this.stop(reason) }, { isStopped })
                Log.d("AkiFramework", "\n---------------------\n[Action Group] ${action.id}")
                action.block.invoke(ctx)
            }
            
            // Xử lý kết quả: Nếu là EndActionException thì coi như thành công và tiếp tục
            val finalResult = if (result.exceptionOrNull() is com.aki.akiwarmup.core.dsl.EndActionException) {
                Log.d("AkiFramework", "Action [${action.id}] ended prematurely via endAction()")
                Result.success(Unit)
            } else {
                result
            }
            
            logger.log(currentScreen.id, action.id, finalResult)
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
        val policy = scene.config.onUnknownScreen
        val timeout = scene.config.recoveryTimeoutMs
        
        // Thử các biện pháp nhẹ trước (delay/back)
        if (count < 3) {
            humanEngine.gaussianDelay(1000, 200)
            return
        }

        // Nếu vượt quá timeout hoặc count quá lớn, thực hiện restart theo policy
        val totalWaitEstimate = count * 1500L
        if (totalWaitEstimate > timeout || count >= 10) {
            restartTargetApp()
        } else {
            when (policy) {
                UnknownScreenPolicy.PRESS_BACK -> device.pressBack()
                UnknownScreenPolicy.WAIT -> delay(2000)
                UnknownScreenPolicy.RESTART_APP -> restartTargetApp()
            }
        }
    }

    private fun restartTargetApp() {
        val pkg = scene.config.targetPackage
        if (pkg.isNotEmpty()) {
            consecutiveRestarts++
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
