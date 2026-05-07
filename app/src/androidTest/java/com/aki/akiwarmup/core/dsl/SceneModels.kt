package com.aki.akiwarmup.core.dsl

import androidx.test.uiautomator.UiDevice

data class Scene(
    val name: String,
    val screens: List<ScreenDef>,
    val config: SceneConfig
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

class ActionExecutionContext(
    val device: UiDevice,
    val humanEngine: com.aki.akiwarmup.core.human.HumanBehaviorEngine,
    val sceneConfig: SceneConfig,
    val runConfig: com.aki.akiwarmup.core.config.RunConfig,
    val stopSignal: () -> Unit = {}
)
