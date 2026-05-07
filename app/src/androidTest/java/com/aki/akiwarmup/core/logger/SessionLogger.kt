package com.aki.akiwarmup.core.logger

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder

data class LogEntry(
    val timestamp: Long,
    val screenId: String,
    val actionId: String,
    val success: Boolean,
    val error: String? = null
)

class SessionLogger {
    private val entries = mutableListOf<LogEntry>()
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val TAG = "AKI_FRAMEWORK"

    fun log(screenId: String, actionId: String, result: Result<Unit>) {
        val entry = LogEntry(
            timestamp = System.currentTimeMillis(),
            screenId = screenId,
            actionId = actionId,
            success = result.isSuccess,
            error = result.exceptionOrNull()?.message
        )
        entries.add(entry)
        
        // Real-time status to logcat for the runner to pick up
        val status = if (result.isSuccess) "SUCCESS" else "FAILED: ${result.exceptionOrNull()?.message}"
        Log.i(TAG, "ACTION: [$screenId] -> [$actionId] | $status")
    }

    fun getFullReport(): String {
        return gson.toJson(entries)
    }

    fun finalize() {
        val report = getFullReport()
        // Special tag to denote the end of session and the full report
        Log.i(TAG, "SESSION_COMPLETE_REPORT_START")
        Log.i(TAG, report)
        Log.i(TAG, "SESSION_COMPLETE_REPORT_END")
    }
}
