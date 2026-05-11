package com.aki.akiwarmup.core.dsl

import android.app.Instrumentation
import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.aki.akiwarmup.core.human.HumanBehaviorEngine

data class Scene(
    val name: String,
    val screens: List<ScreenDef>,
    val unknownScreenHandler: (suspend () -> Unit)? = null,
    val context: SceneExecutionContext
)

data class SceneConfig(
    var targetPackage: String = "",
    var onUnknownScreen: UnknownScreenPolicy = UnknownScreenPolicy.PRESS_BACK,
    var recoveryTimeoutMs: Long = 10000L,
    var watchVideoSeconds: IntRange = 5..45,
    var likeRate: Float = 0.15f,
    var followRate: Float = 0.05f,
    var searchRate: Float = 0.10f,
    var keywords: List<String> = emptyList()
)

enum class UnknownScreenPolicy {
    PRESS_BACK, WAIT, RESTART_APP
}

data class ScreenDef(
    val id: String,
    val detectPredicate: DetectPredicate,
    val actions: List<ActionDef>
)

class DetectPredicate(val check: (UiDevice) -> Boolean) {
    fun evaluate(device: UiDevice) = check(device)
    
    infix fun or(other: DetectPredicate) = DetectPredicate { 
        this.evaluate(it) || other.evaluate(it) 
    }
    
    infix fun and(other: DetectPredicate) = DetectPredicate { 
        this.evaluate(it) && other.evaluate(it) 
    }
}

data class ActionDef(
    val id: String,
    val weight: Int,
    val block: suspend () -> Unit
)

open class AkiContext(
    val instrumentation: Instrumentation = InstrumentationRegistry.getInstrumentation(),
    val androidContext: Context = instrumentation.targetContext,
    val device: UiDevice = UiDevice.getInstance(instrumentation),
    var stopSignal: (String) -> Unit = {},
    val humanBehaviorEngine: HumanBehaviorEngine,
    val sceneConfig: SceneConfig = SceneConfig()
) {
    var stopped: Boolean = false
    var isStopped: () -> Boolean = { stopped }

    constructor(akiContext: AkiContext): this(
        akiContext.instrumentation,
        akiContext.androidContext,
        akiContext.device,
        akiContext.stopSignal,
        akiContext.humanBehaviorEngine,
        akiContext.sceneConfig
    ) {
        this.isStopped = akiContext.isStopped
    }

    val args = InstrumentationRegistry.getArguments()

    fun stop(reason: String) {
        stopped = true
        stopSignal(reason)
    }
}

open class SceneExecutionContext(
    val baseContext: AkiContext,
    var restartCount: Int = 0,
    var consecutiveRestarts: Int = 0,
    var consecutiveUnknownScreens: Int = 0
) {
    val device get() = baseContext.device
    val isStopped get() = baseContext.isStopped
    val sceneConfig get() = baseContext.sceneConfig
    val instrumentation get() = baseContext.instrumentation
    val androidContext get() = baseContext.androidContext
    val humanBehaviorEngine get() = baseContext.humanBehaviorEngine
    val stopSignal get() = baseContext.stopSignal
    
    fun stop(reason: String) {
        baseContext.stop(reason)
    }
}

class ActionExecutionContext(
    val sceneContext: SceneExecutionContext
) {
    val baseContext get() = sceneContext.baseContext
    val device get() = sceneContext.device
    val isStopped get() = sceneContext.isStopped
    val sceneConfig get() = sceneContext.sceneConfig
    val instrumentation get() = sceneContext.instrumentation
    val androidContext get() = sceneContext.androidContext
    val humanBehaviorEngine get() = sceneContext.humanBehaviorEngine
    val stopSignal get() = sceneContext.stopSignal
    
    fun stop(reason: String) {
        sceneContext.stop(reason)
    }
}
