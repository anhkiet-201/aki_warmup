package com.aki.akiwarmup.core.dsl

import androidx.test.uiautomator.UiObject2

fun defineAction(
    id: String,
    context: SceneExecutionContext,
    block: suspend ActionBuilder.() -> Unit
): ActionDef = ActionDef(id) {
    ActionBuilder(context).block()
}

val activeSceneBuilder = ThreadLocal<SceneBuilder>()

fun defineScreen(
    id: String,
    context: SceneExecutionContext,
    priority: Int = 0,
    block: ScreenBuilder.() -> Unit
): Unit {
    val screenDef = ScreenBuilder(id, context).apply {
        this.priority = priority
        block()
    }.build()
    
    activeSceneBuilder.get()?.screen { screenDef }
}

fun defineScene(id: String, context: AkiContext, block: SceneBuilder.() -> Unit): Scene {
    val builder = SceneBuilder(id, context)
    val oldBuilder = activeSceneBuilder.get()
    activeSceneBuilder.set(builder)
    try {
        builder.block()
    } finally {
        if (oldBuilder != null) {
            activeSceneBuilder.set(oldBuilder)
        } else {
            activeSceneBuilder.remove()
        }
    }
    return builder.build()
}

fun UiObject2?.toName(): String {
    return this?.text
        ?: this?.contentDescription
        ?: this?.hint
        ?: this?.className
        ?: this?.resourceName
        ?: "null"
}