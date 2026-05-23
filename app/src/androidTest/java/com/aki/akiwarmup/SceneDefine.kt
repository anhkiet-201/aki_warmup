package com.aki.akiwarmup

import android.util.Log
import com.aki.akiwarmup.core.dsl.AkiContext
import com.aki.akiwarmup.core.dsl.SceneBuilder
import com.aki.akiwarmup.core.dsl.SceneConfig
import com.aki.akiwarmup.core.dsl.ScreenBuilder
import com.aki.akiwarmup.core.dsl.UnknownScreenPolicy
import com.aki.akiwarmup.core.dsl.defineScene


val tiktokConfig = SceneConfig(
    targetPackage = "com.ss.android.ugc.trill",
    onUnknownScreen = UnknownScreenPolicy.PRESS_BACK,
    recoveryTimeoutMs = 15000L
)

fun tiktokSceneDefine(id: String, context: AkiContext, block: SceneBuilder.() -> Unit) = defineScene(id, context) {
    config(tiktokConfig)
    block()
}

fun tiktokWarmUpScene(context: AkiContext, block: SceneBuilder.() -> Unit) = tiktokSceneDefine("Tiktok WarmUp", context) {
    handleUnknowScreen {
        Log.i("AkiFramework", "${this.context.restartCount}")
        if(this.context.restartCount > 3) {
            this.context.stop("lỖI APP")
        }
    }
    this.apply {
        block()
    }
}