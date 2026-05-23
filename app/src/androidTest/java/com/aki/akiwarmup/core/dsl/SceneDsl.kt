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
    private val actions = mutableListOf<ActionDef>()

    fun detect(block: DetectBuilder.() -> DetectPredicate) {
        detectPredicate = DetectBuilder().block()
    }

    fun action(id: String, weight: Int = 1, block: suspend ActionBuilder.() -> Unit) {
        actions.add(ActionDef(id, weight) {
            ActionBuilder(context).block()
        })
    }

    fun action(block: () -> ActionDef) = actions.add(block())


    fun build() = ScreenDef(screenID, detectPredicate ?: DetectPredicate { false }, actions)
}
