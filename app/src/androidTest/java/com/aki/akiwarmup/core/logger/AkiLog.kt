package com.aki.akiwarmup.core.logger

import android.util.Log

/**
 * Các prefix phân cấp cho log output của AkiFramework.
 * Mỗi prefix đại diện cho một tầng/layer riêng biệt trong hệ thống.
 */
object LogTag {
    /** Các hành động UI trực tiếp: tap, swipe, type, wait, endAction, v.v. */
    const val ACTION  = "ACTION"
    /** Nhận diện UI element: find, findAll, detect screen. */
    const val DETECT  = "DETECT"
    /** Engine loop: screen detection, action dispatch, restart, stop. */
    const val ENGINE  = "ENGINE"
    /** Nội dung video/bài đăng: caption, keyword match. */
    const val CONTENT = "CONTENT"
    /** Session lifecycle và báo cáo kết quả. */
    const val SESSION = "SESSION"
    /** Vòng lặp iteration. */
    const val LOOP    = "LOOP"
}

/**
 * Singleton logger tập trung với **phân cấp indent tự động** theo execution depth.
 *
 * Thay thế hoàn toàn các lời gọi `android.util.Log` trực tiếp.
 * Filter logcat bằng: `adb logcat -s AKIFRAMEWORK`
 *
 * Cấu trúc phân cấp output (depth được quản lý bởi [ActionLoop]):
 * ```
 * [ENGINE] ■ Screen [Home]           ← depth 0 (resetScope)
 *   [ENGINE] ▶ [Watch Video]         ← depth 1 (enterScope)
 *     [DETECT] find(video_desc) → Found   ← depth 2 (enterScope)
 *     [CONTENT] desc: #ttnhr việc làm...
 *     [CONTENT] keyword hit ✓
 *     [ACTION] tap(like_button)
 *     [ACTION] wait(3000ms)
 *       [LOOP] iter 1/∞             ← depth 3 (khi vào loop body)
 *         [DETECT] find(...) → Found
 * ```
 *
 * Phân cấp level:
 * - [i]: Business event quan trọng (Screen detected, Session report).
 * - [d]: Flow bình thường (Action, tap, wait, find, endAction).
 * - [v]: Chi tiết nhỏ/noisy (loop iteration, choose branch, screen checking).
 * - [w]: Warning / soft-fail (no screen matched, restart, unknown screen).
 * - [e]: Lỗi nghiêm trọng (exception, crash).
 */
object AkiLog {
    private const val TAG = "AKIFRAMEWORK"

    /**
     * Độ sâu indent hiện tại.
     * Quản lý bởi [ActionLoop] qua [resetScope]/[enterScope]/[exitScope].
     * Không cần thread-safe vì test runner chạy single-thread.
     */
    private var depth = 0

    private fun pad() = "  ".repeat(depth)

    /** Đặt lại indent về 0 — gọi đầu mỗi loop iteration trong ActionLoop. */
    internal fun resetScope() { depth = 0 }

    /** Tăng indent — gọi khi vào scope mới (action group, action body, loop body). */
    internal fun enterScope() { depth++ }

    /** Giảm indent — gọi khi thoát scope. */
    internal fun exitScope() { if (depth > 0) depth-- }

    fun i(prefix: String, msg: String) = Log.i(TAG, "${pad()}[$prefix] $msg")
    fun d(prefix: String, msg: String) = Log.d(TAG, "${pad()}[$prefix] $msg")
    fun v(prefix: String, msg: String) = Log.v(TAG, "${pad()}[$prefix] $msg")
    fun w(prefix: String, msg: String) = Log.w(TAG, "${pad()}[$prefix] $msg")
    fun e(prefix: String, msg: String, t: Throwable? = null) = Log.e(TAG, "${pad()}[$prefix] $msg", t)
}
