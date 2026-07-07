package com.aki.akiwarmup.core.logger

import com.aki.akiwarmup.core.logger.AkiLog
import com.aki.akiwarmup.core.logger.LogTag
import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * Đại diện cho một mục nhật ký (log entry) ghi nhận kết quả thực thi của một hành động kiểm thử.
 *
 * @property timestamp Thời gian ghi nhận sự kiện (milliseconds kể từ mốc Epoch).
 * @property screenId Định danh của màn hình đang thực hiện hành động.
 * @property actionId Định danh hoặc mô tả ngắn về hành động được thực hiện.
 * @property success Trạng thái thực thi của hành động (true nếu thành công, false nếu thất bại).
 * @property error Chi tiết thông báo lỗi và vị trí lỗi trong code nếu hành động thất bại.
 */
data class LogEntry(
    val timestamp: Long,
    val screenId: String,
    val actionId: String,
    val success: Boolean,
    val error: String? = null
)

/**
 * Trình ghi log phiên kiểm thử (Session Logger) có nhiệm vụ thu thập, lưu trữ thông tin thực thi
 * của các bước kiểm thử trong suốt một phiên làm việc, hỗ trợ xuất báo cáo chi tiết dạng JSON khi kết thúc.
 */
class SessionLogger {
    private val entries = mutableListOf<LogEntry>()
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    /**
     * Ghi nhận kết quả của một hành động kiểm thử vào danh sách và in ra logcat.
     *
     * @param screenId Định danh của màn hình nơi hành động diễn ra.
     * @param actionId Định danh/tên của hành động vừa thực hiện.
     * @param result Kết quả thực thi dưới dạng [Result] của Kotlin (thành công hoặc có ngoại lệ).
     */
    fun log(screenId: String, actionId: String, result: Result<Unit>) {
        val exception = result.exceptionOrNull()
        val errorDetail = exception?.let { getDetailedErrorMessage(it) }

        val entry = LogEntry(
            timestamp = System.currentTimeMillis(),
            screenId = screenId,
            actionId = actionId,
            success = result.isSuccess,
            error = errorDetail
        )
        entries.add(entry)

        if (result.isSuccess) {
            AkiLog.i(LogTag.SESSION, "ACTION: [$screenId] -> [$actionId] | SUCCESS")
        } else {
            AkiLog.e(LogTag.SESSION, "ACTION: [$screenId] -> [$actionId] | FAILED: $errorDetail")
        }
    }

    /**
     * Trích xuất thông tin lỗi chi tiết từ một đối tượng ngoại lệ [Throwable].
     *
     * Phương thức này phân tích stack trace để tìm dòng code đầu tiên thuộc package "com.aki.akiwarmup"
     * để chỉ ra vị trí chính xác xảy ra lỗi trong mã nguồn của ứng dụng/framework.
     *
     * @param t Ngoại lệ cần định dạng thông báo lỗi.
     * @return Chuỗi định dạng chứa tên class ngoại lệ, thông điệp lỗi và vị trí phát sinh (file, class, dòng).
     */
    private fun getDetailedErrorMessage(t: Throwable): String {
        val className = t::class.java.name
        val message = t.message ?: "No detail message"
        val stackTraceElement = t.stackTrace.firstOrNull { 
            it.className.startsWith("com.aki.akiwarmup") 
        } ?: t.stackTrace.firstOrNull()
        
        val location = stackTraceElement?.let { 
            " at ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})"
        } ?: ""
        
        return "\n$className: \n\t$message\n\t\t$location"
    }

    /**
     * Trả về toàn bộ lịch sử các mục nhật ký của phiên kiểm thử hiện tại dưới dạng chuỗi JSON đẹp (pretty printed).
     *
     * @return Chuỗi JSON chứa danh sách các [LogEntry].
     */
    fun getFullReport(): String {
        return gson.toJson(entries)
    }

    /**
     * Hoàn tất phiên kiểm thử, xuất toàn bộ báo cáo chi tiết (JSON) ra logcat với các thẻ đánh dấu đặc biệt
     * (`SESSION_COMPLETE_REPORT_START` và `SESSION_COMPLETE_REPORT_END`) giúp các công cụ phân tích log bên ngoài dễ dàng trích xuất.
     */
    fun finalize() {
        val report = getFullReport()
        // Special tag to denote the end of session and the full report
        AkiLog.i(LogTag.SESSION, "SESSION_COMPLETE_REPORT_START")
        AkiLog.i(LogTag.SESSION, report)
        AkiLog.i(LogTag.SESSION, "SESSION_COMPLETE_REPORT_END")
    }
}
