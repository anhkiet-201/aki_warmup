package com.aki.akiwarmup.core.dsl

@DslMarker
annotation class SceneDslMarker

@SceneDslMarker
class SceneBuilder(val name: String) {
    private val screens = mutableListOf<ScreenDef>()
    var sceneConfig = SceneConfig()
    
    fun config(block: SceneConfig.() -> Unit) {
        sceneConfig.apply(block)
    }
    
    fun screen(id: String, block: ScreenBuilder.() -> Unit) {
        screens.add(ScreenBuilder(id).apply(block).build())
    }
    
    fun build() = Scene(name, screens, sceneConfig)
}

@SceneDslMarker
class ScreenBuilder(val screenID: String) {
    private var detectPredicate: DetectPredicate? = null
    private val actions = mutableListOf<ActionDef>()
    
    fun detect(block: DetectBuilder.() -> DetectPredicate) {
        detectPredicate = DetectBuilder().block()
    }
    
    fun actions(block: ActionsBuilder.() -> Unit) {
        ActionsBuilder(actions).apply(block)
    }
    
    fun build() = ScreenDef(screenID, detectPredicate ?: DetectPredicate { false }, actions)
}

@SceneDslMarker
class ActionsBuilder(private val list: MutableList<ActionDef>) {
    fun action(id: String, weight: Int, block: suspend ActionBlock.() -> Unit) {
        list.add(ActionDef(id, weight) {
            ActionBlock(this).block()
        })
    }
}

fun scene(name: String, block: SceneBuilder.() -> Unit): Scene {
    return SceneBuilder(name).apply(block).build()
}
