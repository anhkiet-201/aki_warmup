package com.aki.akiwarmup.tiktok.model

enum class RateType {
    SWIPE, LIKE, COMMENT, FAVORITE, RE_POST, COPY_LINK, EXIT
}

/**
 * Quản lý tỉ lệ hành động theo trọng số.
 * Map-based, không boilerplate: thêm RateType mới chỉ cần thêm enum value.
 */
class AutoRate(
    private val initial: Map<RateType, Int> = mapOf(
        RateType.SWIPE to 40,
        RateType.LIKE to 20,
        RateType.COMMENT to 10,
        RateType.FAVORITE to 20,
        RateType.RE_POST to 10,
        RateType.COPY_LINK to 10,
        RateType.EXIT to 5
    ),
    private val step: Int = 10
) {
    private val current = initial.toMutableMap()

    operator fun get(type: RateType): Int = current[type] ?: 0

    /**
     * Giảm dần xác suất của một loại hành động sau khi đã thực hiện thành công.
     * Mục đích: Giảm tần suất lặp lại liên tục cùng một hành động trong suốt thời gian xem một video.
     */
    fun consume(type: RateType) {
        val v = current[type] ?: return
        current[type] = if (v >= step) v - step else 0
    }

    /**
     * Thiết lập hoặc điều chỉnh lại giá trị xác suất (rate) cố định cho một loại hành động.
     * Hữu ích khi cần ép một hành động (như SWIPE hay EXIT) có xác suất cao hơn bình thường trong một số trường hợp cụ thể.
     */
    fun reserve(type: RateType, value: Int) {
        current[type] = value
    }

    /**
     * Đặt toàn bộ rate về 0 trừ SWIPE.
     * Dùng khi nội dung video không phù hợp — chỉ muốn swipe qua.
     */
    fun swipeBias() {
        RateType.entries.filter { it != RateType.SWIPE }.forEach { current[it] = 0 }
    }

    fun reset() {
        current.putAll(initial)
    }
}
