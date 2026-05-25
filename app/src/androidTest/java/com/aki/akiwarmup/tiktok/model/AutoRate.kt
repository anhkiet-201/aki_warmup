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
        RateType.SWIPE to 4,
        RateType.LIKE to 2,
        RateType.COMMENT to 1,
        RateType.FAVORITE to 2,
        RateType.RE_POST to 1,
        RateType.COPY_LINK to 1,
        RateType.EXIT to 1
    ),
    private val step: Int = 1
) {
    private val current = initial.toMutableMap()

    operator fun get(type: RateType): Int = current[type] ?: 0

    fun consume(type: RateType) {
        val v = current[type] ?: return
        if (v >= step) current[type] = v - step
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
