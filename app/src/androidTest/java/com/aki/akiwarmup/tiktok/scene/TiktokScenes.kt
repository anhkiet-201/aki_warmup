package com.aki.akiwarmup.tiktok.scene

import com.aki.akiwarmup.core.dsl.AkiContext
import com.aki.akiwarmup.core.dsl.SceneBuilder
import com.aki.akiwarmup.core.dsl.SceneConfig
import com.aki.akiwarmup.core.dsl.UnknownScreenPolicy
import com.aki.akiwarmup.core.dsl.defineScene

val tiktokConfig = SceneConfig(
    targetPackage = "com.ss.android.ugc.trill",
    onUnknownScreen = UnknownScreenPolicy.PRESS_BACK,
    recoveryTimeoutMs = 15000L
)

/**
 * Wrapper tạo Scene với cấu hình TikTok mặc định.
 */
fun tiktokSceneDefine(id: String, context: AkiContext, block: SceneBuilder.() -> Unit) =
    defineScene(id, context) {
        config(tiktokConfig)
        block()
    }
