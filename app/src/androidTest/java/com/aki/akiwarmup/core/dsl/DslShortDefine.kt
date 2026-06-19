package com.aki.akiwarmup.core.dsl

import androidx.test.uiautomator.UiObject2

fun defineAction(
    id: String,
    context: SceneExecutionContext,
    block: suspend ActionBuilder.() -> Unit
): ActionDef = ActionDef(id) {
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

fun UiObject2?.toName(): String {
    return this?.text
        ?: this?.contentDescription
        ?: this?.hint
        ?: this?.className
        ?: this?.resourceName
        ?: "null"
}