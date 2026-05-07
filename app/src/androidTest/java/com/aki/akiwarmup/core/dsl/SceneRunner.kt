package com.aki.akiwarmup.core.dsl

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.aki.akiwarmup.core.config.AdbConfigBridge
import com.aki.akiwarmup.core.config.RunConfig
import com.aki.akiwarmup.core.human.HumanBehaviorEngine
import com.aki.akiwarmup.core.logger.SessionLogger
import com.aki.akiwarmup.core.loop.ActionLoop
import com.aki.akiwarmup.core.screen.ScreenDetector
import kotlinx.coroutines.runBlocking

/**
 * SceneRunner cung cấp các lệnh điều khiển cao cấp cho một Scene.
 */
class SceneRunner(
    val device: UiDevice,
    private val scene: Scene,
    private val logger: SessionLogger,
    private val humanEngine: HumanBehaviorEngine,
    private val runConfig: RunConfig
) {
    private val detector = ScreenDetector(device, scene.screens)
    private val loopManager = ActionLoop(device, scene, detector, humanEngine, logger, runConfig)

    init {
        // Tối ưu hóa UI Automator cho các ứng dụng video (không đợi idle quá lâu)
        androidx.test.uiautomator.Configurator.getInstance().apply {
            waitForIdleTimeout = 200L
            actionAcknowledgmentTimeout = 200L
            scrollAcknowledgmentTimeout = 200L
            waitForSelectorTimeout = 1000L
        }
    }

    /**
     * Khởi chạy ứng dụng mục tiêu của Scene bằng lệnh monkey.
     */
    fun launchApp() {
        val pkg = scene.config.targetPackage
        if (pkg.isNotEmpty()) {
            device.executeShellCommand("monkey -p $pkg -c android.intent.category.LAUNCHER 1")
            // Chờ ứng dụng lên
            Thread.sleep(5000)
        }
    }

    /**
     * Chạy vòng lặp hành động.
     * @param iterations Số lần lặp (-1 là vô hạn cho đến khi hết thời gian hoặc gọi stop)
     * @param afterIteration Callback chạy sau mỗi bước hành động.
     */
    suspend fun loop(iterations: Int = -1, afterIteration: suspend () -> Unit = {}) {
        loopManager.run(iterations, afterIteration)
    }

    /**
     * Chạy đúng 1 hành động duy nhất.
     */
    suspend fun run(afterIteration: suspend () -> Unit = {}) {
        loop(1, afterIteration)
    }

    /**
     * Dừng vòng lặp hiện tại.
     */
    fun stop() {
        loopManager.stop()
    }
}

/**
 * Hàm khởi chạy Scene DSL.
 */
fun runScene(scene: Scene, block: suspend SceneRunner.(SceneRunner) -> Unit) = runBlocking {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val device = UiDevice.getInstance(instrumentation)
    val logger = SessionLogger()
    val humanEngine = HumanBehaviorEngine()
    val runConfig = AdbConfigBridge.load()

    val runner = SceneRunner(device, scene, logger, humanEngine, runConfig)
    try {
        runner.block(runner)
    } finally {
        logger.finalize()
    }
}
