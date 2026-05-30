package com.aki.akiwarmup.core.engine

import com.aki.akiwarmup.core.logger.AkiLog
import com.aki.akiwarmup.core.logger.LogTag
import com.aki.akiwarmup.core.dsl.ActionDef
import com.aki.akiwarmup.core.dsl.Scene
import com.aki.akiwarmup.core.dsl.UnknownScreenPolicy
import com.aki.akiwarmup.core.logger.SessionLogger
import kotlinx.coroutines.delay
import kotlin.random.Random

class ActionLoop(
    private val scene: Scene,
    private val detector: ScreenDetector,
    private val logger: SessionLogger,
) {
    private val context = scene.context
    private val device = context.device
    private val humanEngine = context.humanBehaviorEngine
    private val random = Random

    fun stop(reason: String = "", resultCode: Int = android.app.Activity.RESULT_OK) {
        if (reason.isNotEmpty()) {
            AkiLog.d(LogTag.ENGINE, "Stop: $reason")
        }
        context.stop(reason, resultCode)
    }

    suspend fun run(iterations: Int = -1, onIterationComplete: suspend () -> Unit = {}) {
        if (scene.screens.isEmpty()) return
        var currentIteration = 0

        while (!context.isStopped() && (iterations == -1 || currentIteration < iterations)) {
            AkiLog.resetScope()
            val currentScreen = detector.detectCurrent()

            if (currentScreen == null) {
                context.consecutiveUnknownScreens++

                handleUnknownScreen(context.consecutiveUnknownScreens)
                scene.unknownScreenHandler?.invoke()

                // Nếu bị kẹt quá lâu mà không có screen nào, dừng để tránh loop vô tận
                if (scene.context.consecutiveUnknownScreens > 20 && scene.screens.isEmpty()) {
                    stop("No screens defined and stuck too long")
                }
                continue
            }

            scene.context.consecutiveUnknownScreens = 0
            scene.context.consecutiveRestarts = 0
            val action = selectWeightedAction(currentScreen.actions)

            // Depth 0: Screen
            AkiLog.i(LogTag.ENGINE, "■ Screen [${currentScreen.id}]")
            AkiLog.enterScope()          // depth 1
            AkiLog.d(LogTag.ENGINE, "▶ [${action.id}]")
            AkiLog.enterScope()          // depth 2: body của action

            val result = runCatching {
                action.block.invoke()
            }

            // Xử lý EndActionException như là kết quả thành công
            val finalResult = if (result.exceptionOrNull() is com.aki.akiwarmup.core.dsl.EndActionException) {
                AkiLog.d(LogTag.ACTION, "endAction()")
                Result.success(Unit)
            } else {
                result
            }
            AkiLog.exitScope()           // depth 1
            AkiLog.exitScope()           // depth 0

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
        val policy = context.sceneConfig.onUnknownScreen
        val timeout = context.sceneConfig.recoveryTimeoutMs

        // Thử các biện pháp nhẹ trước (chờ ngắn)
        if (count < 3) {
            humanEngine.gaussianDelay(1000, 200)
            return
        }

        // Nếu vượt timeout hoặc count quá lớn, restart app theo policy
        val totalWaitEstimate = count * 1500L
        if (totalWaitEstimate > timeout || count >= 10) {
            restartTargetApp()
        } else {
            when (policy) {
                UnknownScreenPolicy.PRESS_BACK  -> device.pressBack()
                UnknownScreenPolicy.WAIT        -> delay(2000)
                UnknownScreenPolicy.RESTART_APP -> restartTargetApp()
            }
        }
    }

    private fun restartTargetApp() {
        val pkg = context.sceneConfig.targetPackage
        if (pkg.isNotEmpty()) {
            scene.context.restartCount++
            scene.context.consecutiveRestarts++
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
