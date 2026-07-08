package com.aki.akiwarmup.core.dsl

import android.graphics.Point
import android.os.Message
import com.aki.akiwarmup.core.logger.AkiLog
import com.aki.akiwarmup.core.logger.LogTag
import androidx.test.uiautomator.UiObject2
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class ScrollDirection {
    Up,
    Down,
    Left,
    Right
}
class ActionBuilder(val context: SceneExecutionContext) {
    private val humanEngine = context.humanBehaviorEngine
    private val device = context.device
    private val random = Random

    suspend fun wait(ms: Long) {
        AkiLog.d(LogTag.ACTION, "wait(${ms}ms)")
        delay(ms)
    }

    suspend fun wait(milliseconds: Int) {
        AkiLog.d(LogTag.ACTION, "wait(${milliseconds}ms)")
        delay(milliseconds.toLong())
    }

    fun find(selector: Selector): UiObject2? = selector.find(device)

    suspend fun on(selector: Selector, block: suspend (UiObject2?) -> Unit) = block(find(selector))

    fun has(selector: Selector): Boolean = selector.exists(device)

    /**
     * Đợi linh hoạt cho đến khi UI element xuất hiện hoặc hết thời gian.
     */
    suspend fun waitUntil(
        selector: Selector,
        maxMs: Long = 60000L,
        intervalMs: Long = 300L
    ): UiObject2? {
        AkiLog.d(LogTag.ACTION, "waitUntil($selector, max=${maxMs}ms)")
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < maxMs && !context.isStopped()) {
            if (selector.exists(device)) {
                return find(selector)
            }
            delay(intervalMs)
        }
        AkiLog.w(LogTag.ACTION, "waitUntil timeout for $selector")
        return null
    }

    fun find(
        resourceId: String? = null,
        text: String? = null,
        desc: String? = null,
        className: String? = null,
        parent: UiObject2? = null
    ): UiObject2? {
        val s = SimpleSelector()
        resourceId?.let { s.id(it) }
        text?.let { s.text(it) }
        desc?.let { s.desc(it) }
        className?.let { s.clazz(it) }
        
        return if (parent != null) s.find(parent) else s.find(device)
    }

    fun findAll(selector: Selector): List<UiObject2> = selector.findAll(device)

    fun findAll(resourceId: String? = null, text: String? = null): List<UiObject2> {
        val s = SimpleSelector()
        resourceId?.let { s.id(it) }
        text?.let { s.text(it) }
        return s.findAll(device)
    }

    fun tap(target: UiObject2?, humanized: Boolean = true) {
        val label = target.toName()
        val humanStr = if (humanized) "" else " raw"
        AkiLog.d(LogTag.ACTION, "tap($label$humanStr)")
        target?.let {
            val center = it.visibleCenter
            val point = if (humanized) humanEngine.getScatterPoint(center, 10) else center
            device.click(point.x, point.y)
        }
    }

    suspend fun doubleTap(center: Point, scatter: Int = 15) {
        AkiLog.d(LogTag.ACTION, "doubleTap(center=$center)")
        val p1 = humanEngine.getScatterPoint(center, scatter)
        device.click(p1.x, p1.y)
        delay(100)
        val p2 = humanEngine.getScatterPoint(center, scatter)
        device.click(p2.x, p2.y)
    }

    fun swipeUp(humanized: Boolean = true) {
        AkiLog.d(LogTag.ACTION, "swipeUp(humanized=$humanized)")
        val width = device.displayWidth
        val height = device.displayHeight
        val from = Point(width / 2 + random.nextInt(50) + 25, (height * ((random.nextInt(6, 8)) / 10f )).toInt())
        val to = Point(width / 2 + random.nextInt(50) + 25, (height * (random.nextInt(2,4) / 10f)).toInt())
        
        if (humanized) {
            humanEngine.humanSwipe(device, from, to)
        } else {
            device.swipe(from.x, from.y, to.x, to.y, 10)
        }
    }

    fun scroll(direction: ScrollDirection, distancePx: Int) {
        AkiLog.d(LogTag.ACTION, "scroll($direction, ${distancePx}px)")
        val width = device.displayWidth
        val height = device.displayHeight
        val startX = width / 2
        val startY = height / 2
        
        when (direction) {
            ScrollDirection.Down -> device.swipe(startX, startY, startX, startY - distancePx, 10)
            ScrollDirection.Up -> device.swipe(startX, startY, startX, startY + distancePx, 10)
            ScrollDirection.Left -> device.swipe(startX, startY, startX + distancePx, startY, 10)
            ScrollDirection.Right -> device.swipe(startX, startY, startX - distancePx, startY, 10)
        }
    }

    suspend fun humanType(field: UiObject2?, text: String) {
        val fieldLabel = field.toName()
        AkiLog.d(LogTag.ACTION, "type($fieldLabel, '${text.take(25)}'")
        field?.let { humanEngine.humanType(it, text) }
    }

    fun pressBack() {
        AkiLog.d(LogTag.ACTION, "pressBack()")
        device.pressBack()
    }

    fun pressEnter() {
        AkiLog.d(LogTag.ACTION, "pressEnter()")
        device.pressEnter()
    }

    fun pressHome() {
        AkiLog.d(LogTag.ACTION, "pressHome()")
        device.pressHome()
    }

    fun microScroll() {
        AkiLog.d(LogTag.ACTION, "microScroll()")
        val dist = random.nextInt(100) + 50
        if (random.nextBoolean()) {
            scroll(ScrollDirection.Down, dist)
        } else {
            scroll(ScrollDirection.Up, dist)
        }
    }

    /**
     * Thực thi [block] với xác suất [chancePercent]% (0..100).
     */
    suspend fun sometimes(chancePercent: Int, block: suspend () -> Unit) {
        require(chancePercent in 0..100) { "chancePercent must be in [0, 100], got $chancePercent" }
        if (random.nextInt(100) < chancePercent) {
            block()
        }
    }

    fun stop(message: String = "Complete") {
        AkiLog.d(LogTag.ACTION, "stop($message)")
        context.stop(message)
    }

    /**
     * Thoát khỏi Action Group hiện tại ngay lập tức.
     */
    fun endAction() {
        AkiLog.d(LogTag.ACTION, "endAction()")
        throw EndActionException()
    }

    /**
     * Chọn ngẫu nhiên một trong các khối lệnh dựa trên trọng số nguyên.
     *
     * Thuật toán "subtract-and-check" với Int:
     * - Lọc bỏ các nhánh có weight <= 0 trước khi xử lý.
     * - Sinh 1 số rand duy nhất trong [0, totalWeight) bằng nextInt — không có floating-point.
     * - Trừ dần trọng số ra khỏi rand; khi rand < 0 thì chọn nhánh đó.
     * - Điều kiện là `< 0` (không phải `<= 0`) vì rand nguyên có thể đúng bằng 0 tại biên.
     */
    suspend fun choose(vararg possibilities: Pair<Int, suspend ActionBuilder.() -> Unit>) {
        val valid = possibilities.filter { it.first > 0 }
        if (valid.isEmpty()) return

        val totalWeight = valid.sumOf { it.first }
        var rand = random.nextInt(totalWeight)

        for ((weight, block) in valid) {
            rand -= weight
            if (rand < 0) {
                AkiLog.v(LogTag.ACTION, "choose(w=$weight)")
                this.block()
                return
            }
        }
    }

    /**
     * Vòng lặp dựa trên điều kiện động.
     * Ví dụ: loop({ !shouldExit }) { ... }
     */
    suspend fun loop(condition: () -> Boolean, block: suspend ActionBuilder.() -> Unit) {
        AkiLog.d(LogTag.LOOP, "loop started")
        while (!context.isStopped() && condition()) {
            AkiLog.enterScope()
            this.block()
            AkiLog.exitScope()
        }
        AkiLog.d(LogTag.LOOP, "loop finished")
    }

    /**
     * Vòng lặp dựa trên số lần.
     */
    suspend fun loop(times: Int? = null, block: suspend ActionBuilder.() -> Unit) {
        var count = 0
        AkiLog.d(LogTag.LOOP, "loop(times=${times ?: "∞"}) started")
        loop({ times == null || count < times }) {
            AkiLog.v(LogTag.LOOP, "iter ${++count}/${times ?: "∞"}")
            this.block()
        }
    }

    /**
     * Lấy số ngẫu nhiên từ 1 đến [max]
     */
    fun random(max: Int): Int = if (max > 0) random.nextInt(1, max + 1) else 1

    /**
     * Lấy số ngẫu nhiên trong khoảng [min] đến [max]
     */
    fun random(min: Int = 1, max: Int): Int = if (max > min) random.nextInt(min, max + 1) else min

    /**
     * Lấy số ngẫu nhiên Long trong khoảng [min] đến [max].
     */
    fun random(min: Long = 1L, max: Long): Long =
        if (max > min) (min..max).random() else min
}
