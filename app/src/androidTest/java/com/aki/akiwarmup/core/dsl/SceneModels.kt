package com.aki.akiwarmup.core.dsl

import androidx.test.uiautomator.UiDevice
import com.aki.akiwarmup.core.config.RunConfig

data class Scene(
    val name: String,
    val screens: List<ScreenDef>,
    val config: SceneConfig,
    val unknownScreenHandler: (suspend SceneExecutionContext.() -> Unit)? = null
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
    val block: suspend ActionExecutionContext.() -> Unit
)

interface AkiContext {
    val context: android.content.Context
    val device: UiDevice
}

open class SceneExecutionContext(
    override val context: android.content.Context,
    override val device: UiDevice,
    val scene: Scene,
    val restartCount: Int,
    val stopSignal: (String) -> Unit = {}
) : AkiContext {
    fun stop(reason: String) {
        stopSignal(reason)
    }
}

class ActionExecutionContext(
    context: android.content.Context,
    device: UiDevice,
    scene: Scene,
    restartCount: Int,
    val currentScreen: ScreenDef,
    val action: ActionDef,
    val humanEngine: com.aki.akiwarmup.core.human.HumanBehaviorEngine,
    val runConfig: RunConfig,
    stopSignal: (String) -> Unit = {},
    val isStopped: () -> Boolean = { false }
) : SceneExecutionContext(context, device, scene, restartCount, stopSignal)
