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
    var keywords: List<String> = emptyList(),
    /** Timeout khi detect màn hình — ngắn để không chờ quá lâu khi screen không match */
    var detectTimeoutMs: Long = 700L,
    /** Timeout khi thực hiện action (find, tap, v.v.) — dài hơn cho phép TikTok load */
    var actionTimeoutMs: Long = 3000L
)

enum class UnknownScreenPolicy {
    PRESS_BACK, WAIT, RESTART_APP
}

data class ScreenDef(
    val id: String,
    val detectPredicate: DetectPredicate,
    val actions: List<ActionDef>,
    /** Screens với priority cao hơn sẽ được detect trước — đặt cao cho screens xuất hiện thường xuyên */
    val priority: Int = 0
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
    val storage: AkiStorage by lazy { AkiStorage(androidContext) }

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

    fun stop(reason: String = "", resultCode: Int = 0) {
        stopped = true
        val reasonStr = reason.ifEmpty { "Stopped" }
        
        val bundle = android.os.Bundle().apply {
            putString("reason", reasonStr)
        }

        instrumentation.finish(resultCode, bundle)
        
        stopSignal(reason)
    }
}

open class SceneExecutionContext(
    val baseContext: AkiContext,
    var restartCount: Int = 0,
    var consecutiveRestarts: Int = 0,
    var consecutiveUnknownScreens: Int = 0
) {
    val storage get() = baseContext.storage
    val device get() = baseContext.device
    val isStopped get() = baseContext.isStopped
    val sceneConfig get() = baseContext.sceneConfig
    val instrumentation get() = baseContext.instrumentation
    val androidContext get() = baseContext.androidContext
    val humanBehaviorEngine get() = baseContext.humanBehaviorEngine
    val stopSignal get() = baseContext.stopSignal

    val args get() = baseContext.args
    
    fun stop(reason: String = "", resultCode: Int = 0) {
        baseContext.stop(reason, resultCode)
    }
}


