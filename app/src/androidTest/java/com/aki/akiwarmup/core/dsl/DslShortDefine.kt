package com.aki.akiwarmup.core.dsl

fun defineAction(
    id: String,
    context: SceneExecutionContext,
    weight: Int = 1,
    block: suspend ActionBuilder.() -> Unit
): ActionDef = ActionDef(id, weight) {
    ActionBuilder(context).block()
}

fun defineScreen(
    id: String,
    context: SceneExecutionContext,
    priority: Int = 0,
    block: ScreenBuilder.() -> Unit
): ScreenDef = ScreenBuilder(id, context).apply {
    this.priority = priority
    block()
}.build()

fun defineScene(id: String, context: AkiContext, block: SceneBuilder.() -> Unit): Scene =
    SceneBuilder(id, context).apply(block).build()