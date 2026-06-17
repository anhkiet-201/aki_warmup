package com.aki.akiwarmup.facebook.scene

import com.aki.akiwarmup.core.dsl.AkiContext
import com.aki.akiwarmup.core.dsl.SceneBuilder
import com.aki.akiwarmup.core.dsl.SceneConfig
import com.aki.akiwarmup.core.dsl.UnknownScreenPolicy
import com.aki.akiwarmup.core.dsl.defineScene

/**
 * Cấu hình mặc định cho Scene của Facebook.
 * Định nghĩa targetPackage là Facebook (com.facebook.katana), cơ chế xử lý màn hình không xác định,
 * và thời gian timeout cho việc khôi phục và nhận diện.
 */
val faceConfig = SceneConfig(
    targetPackage = "com.facebook.katana",
    onUnknownScreen = UnknownScreenPolicy.PRESS_BACK,
    recoveryTimeoutMs = 15000L,
    detectTimeoutMs = 700L
)

/**
 * Hàm wrapper định nghĩa Scene với cấu hình Facebook mặc định.
 *
 * @param id Định danh của Scene.
 * @param context Ngữ cảnh ngữ hệ AkiContext hiện tại.
 * @param block Khối mã dựng Scene bằng SceneBuilder.
 */
fun faceSceneDefine(id: String, context: AkiContext, block: SceneBuilder.() -> Unit) =
    defineScene(id, context) {
        config(faceConfig)
        block()
    }
