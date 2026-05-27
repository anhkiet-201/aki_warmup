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

    fun consume(type: RateType) {
        val v = current[type] ?: return
        if (v >= step) current[type] = v - step else 0
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
