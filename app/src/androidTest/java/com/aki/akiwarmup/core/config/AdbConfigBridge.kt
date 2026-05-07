package com.aki.akiwarmup.core.config

import android.app.Instrumentation
import android.os.Bundle

data class RunConfig(
    val sceneName: String,
    val durationMs: Long,
    val likeRate: Float,
    val followRate: Float,
    val searchRate: Float,
    val keywords: List<String>
)

object AdbConfigBridge {
    fun load(instrumentation: Instrumentation): RunConfig {
        val args: Bundle = instrumentation.arguments
        
        return RunConfig(
            sceneName = args.getString("scene") ?: "tiktok_warmup",
            durationMs = (args.getString("duration")?.toIntOrNull() ?: 30) * 60 * 1000L,
            likeRate = args.getString("likeRate")?.toFloatOrNull() ?: 0.15f,
            followRate = args.getString("followRate")?.toFloatOrNull() ?: 0.05f,
            searchRate = args.getString("searchRate")?.toFloatOrNull() ?: 0.10f,
            keywords = args.getString("keywords")?.split(",") ?: listOf("funny", "cooking", "travel", "pets")
        )
    }
}
