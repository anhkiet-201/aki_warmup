package com.aki.akiwarmup.core.dsl

import com.aki.akiwarmup.core.human.HumanBehaviorEngine
import com.aki.akiwarmup.core.logger.SessionLogger
import com.aki.akiwarmup.core.loop.ActionLoop
import com.aki.akiwarmup.core.screen.ScreenDetector
import kotlinx.coroutines.runBlocking

/**
 * SceneRunner cung cấp các lệnh điều khiển cao cấp cho một Scene.
 */
class SceneRunner(
    private val logger: SessionLogger,
    private val humanEngine: HumanBehaviorEngine,
) {
    val context: AkiContext = AkiContext(humanBehaviorEngine = humanEngine)

    val device = context.device

    private var _scene: Scene? = null


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
     * Chạy vòng lặp hành động.
     * @param iterations Số lần lặp (-1 là vô hạn cho đến khi hết thời gian hoặc gọi stop)
     * @param afterIteration Callback chạy sau mỗi bước hành động.
     */
    suspend fun loop(iterations: Int = -1, afterIteration: suspend () -> Unit = {}) {
        val scene = _scene ?: throw Exception()
        val detector = ScreenDetector(device, scene.screens)
        val loopManager = ActionLoop(context, scene, detector, logger)
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
//    fun stop() {
//        loopManager.stop()
//    }

    fun scene(name: String, block: SceneBuilder.() -> Unit) {
        _scene = SceneBuilder(name, context).apply(block).build()
    }
}

/**
 * Hàm khởi chạy Scene DSL.
 */
fun runScene(block: suspend SceneRunner.() -> Unit) = runBlocking {
    val logger = SessionLogger()
    val humanEngine = HumanBehaviorEngine()
    val runner = SceneRunner(logger, humanEngine)
    try {
        runner.block()
    } finally {
        logger.finalize()
    }
}
