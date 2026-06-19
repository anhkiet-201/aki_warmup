package com.aki.akiwarmup.core.dsl

import kotlin.String

@DslMarker
annotation class SceneDslMarker

@SceneDslMarker
class SceneBuilder(val name: String, val akiContext: AkiContext) {
    val context = SceneExecutionContext(akiContext)

    private val device = context.device
    private val screens = mutableListOf<ScreenDef>()

    fun config(block: SceneConfig.() -> Unit) {
        context.sceneConfig.apply(block)
    }

    fun config(config: SceneConfig) {
        context.sceneConfig.apply {
            targetPackage = config.targetPackage
            onUnknownScreen = config.onUnknownScreen
            recoveryTimeoutMs = config.recoveryTimeoutMs
            watchVideoSeconds = config.watchVideoSeconds
            likeRate = config.likeRate
            followRate = config.followRate
            searchRate = config.searchRate
            keywords = config.keywords
        }
    }

    fun screen(id: String, block: ScreenBuilder.() -> Unit) {
        screens.add(ScreenBuilder(id, context).apply(block).build())
    }

    fun screen(block: () -> ScreenDef) {
        screens.add(block())
    }

    var unknownScreenHandler: (suspend () -> Unit)? = null

    fun handleUnknowScreen(block: suspend () -> Unit) {
        unknownScreenHandler = block
    }

    fun build() = Scene(name, screens, unknownScreenHandler, context)

    fun include(behavior: SceneBuilder.() -> Unit) {
        this.behavior()
    }


    fun launchApp() {
        val pkg = context.sceneConfig.targetPackage
        if (pkg.isNotEmpty()) {
            device.executeShellCommand("monkey -p $pkg -c android.intent.category.LAUNCHER 1")
            Thread.sleep(5000)
        }
    }

    fun killApp() {
        val pkg = context.sceneConfig.targetPackage
        if (pkg.isNotEmpty()) {
            device.executeShellCommand("am force-stop $pkg")
            Thread.sleep(5000)
        }
    }

    fun restartTargetApp() {
        killApp()
        launchApp()
    }
}

@SceneDslMarker
class ScreenBuilder(val screenID: String, val context: SceneExecutionContext) {
    private var detectPredicate: DetectPredicate? = null
    private var actionProvider: (() -> ActionDef?)? = null
    var priority: Int = 0

    fun detect(block: DetectBuilder.() -> DetectPredicate) {
        detectPredicate = DetectBuilder().block()
    }

    fun action(id: String, block: suspend ActionBuilder.() -> Unit) {
        actionProvider = {
            ActionDef(id) {
                ActionBuilder(context).block()
            }
        }
    }

    fun action(block: () -> ActionDef?) {
        actionProvider = block
    }


    fun build() = ScreenDef(screenID, detectPredicate ?: DetectPredicate { false }, actionProvider ?: { null }, priority)
}
