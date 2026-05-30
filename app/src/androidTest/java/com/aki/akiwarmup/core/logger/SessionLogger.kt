package com.aki.akiwarmup.core.logger

import com.aki.akiwarmup.core.logger.AkiLog
import com.aki.akiwarmup.core.logger.LogTag
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
        AkiLog.i(LogTag.SESSION, "ACTION: [$screenId] -> [$actionId] | $status")
    }

    fun getFullReport(): String {
        return gson.toJson(entries)
    }

    fun finalize() {
        val report = getFullReport()
        // Special tag to denote the end of session and the full report
        AkiLog.i(LogTag.SESSION, "SESSION_COMPLETE_REPORT_START")
        AkiLog.i(LogTag.SESSION, report)
        AkiLog.i(LogTag.SESSION, "SESSION_COMPLETE_REPORT_END")
    }
}
