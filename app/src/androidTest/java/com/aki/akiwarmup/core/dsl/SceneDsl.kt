package com.aki.akiwarmup.core.dsl

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
    
    fun screen(id: String, block: ScreenBuilder.() -> Unit) {
        screens.add(ScreenBuilder(id, context).apply(block).build())
    }
    
    fun screen(screenDef: ScreenDef) {
        screens.add(screenDef)
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
    
//    fun actions(block: ActionsBuilder.() -> Unit) {
//        ActionsBuilder(actions).apply(block)
//    }

    fun action(id: String, weight: Int = 1, block: suspend ActionBuilder.() -> Unit) {
        actions.add(ActionDef(id, weight) {
            ActionBuilder(context,).block()
        })
    }

    fun action(actionDef: ActionDef, weight: Int? = null) {
        actions.add(if (weight != null) actionDef.copy(weight = weight) else actionDef)
    }
    
    fun build() = ScreenDef(screenID, detectPredicate ?: DetectPredicate { false }, actions)
}

//@SceneDslMarker
//class ActionsBuilder(private val list: MutableList<ActionDef>) {
//
//}



//fun defineScreen(id: String, block: ScreenBuilder.() -> Unit): ScreenDef {
//    return ScreenBuilder(id).apply(block).build()
//}

//fun defineAction(id: String, weight: Int = 1, block: suspend ActionBlock.() -> Unit): ActionDef {
//    return ActionDef(id, weight) {
//        ActionBlock(this).block()
//    }
//}
