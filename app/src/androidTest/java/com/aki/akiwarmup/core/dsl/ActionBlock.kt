package com.aki.akiwarmup.core.dsl

import android.graphics.Point
import android.util.Log
import androidx.test.uiautomator.UiObject2
import kotlinx.coroutines.delay
import java.util.Random

enum class ScrollDirection {
    Up,
    Down,
    Left,
    Right
}
class ActionBuilder(sceneExecutionContext: SceneExecutionContext) {
    val context = ActionExecutionContext(sceneExecutionContext)
    private val humanEngine = context.humanBehaviorEngine
    private val device = context.device
    private val random = Random()

    suspend fun wait(ms: Long) {
        Log.d("AkiFramework", "[Action] wait(ms=$ms)")
        delay(ms)
    }

    suspend fun wait(milliseconds: Int) {
        Log.d("AkiFramework", "[Action] wait(milliseconds=$milliseconds)")
        delay(milliseconds.toLong())
    }

    fun find(selector: Selector): UiObject2? = selector.find(device)

    fun has(selector: Selector): Boolean = selector.exists(device)

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
        Log.d("AkiFramework", "[Action] tap(target=${target?.resourceName ?: "null"}, humanized=$humanized)")
        target?.let {
            val center = it.visibleCenter
            val point = if (humanized) humanEngine.getScatterPoint(center, 10) else center
            device.click(point.x, point.y)
        }
    }

    suspend fun doubleTap(center: Point, scatter: Int = 15) {
        Log.d("AkiFramework", "[Action] doubleTap(center=$center)")
        val p1 = humanEngine.getScatterPoint(center, scatter)
        device.click(p1.x, p1.y)
        delay(100)
        val p2 = humanEngine.getScatterPoint(center, scatter)
        device.click(p2.x, p2.y)
    }

    fun swipeUp(humanized: Boolean = true) {
        Log.d("AkiFramework", "[Action] swipeUp(humanized=$humanized)")
        val width = device.displayWidth
        val height = device.displayHeight
        val from = Point(width / 2 + random.nextInt(100) - 50, (height * 0.8).toInt())
        val to = Point(width / 2 + random.nextInt(100) - 50, (height * 0.2).toInt())
        
        if (humanized) {
            humanEngine.humanSwipe(device, from, to)
        } else {
            device.swipe(from.x, from.y, to.x, to.y, 5)
        }
    }

    fun scroll(direction: ScrollDirection, distancePx: Int) {
        Log.d("AkiFramework", "[Action] scroll(direction=$direction, distance=$distancePx)")
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
        Log.d("AkiFramework", "[Action] humanType(field=${field?.resourceName ?: "null"}, text='$text')")
        field?.let { humanEngine.humanType(it, text) }
    }

    fun pressBack() {
        Log.d("AkiFramework", "[Action] pressBack()")
        device.pressBack()
    }

    fun pressEnter() {
        Log.d("AkiFramework", "[Action] pressEnter()")
        device.pressEnter()
    }

    fun pressHome() {
        Log.d("AkiFramework", "[Action] pressHome()")
        device.pressHome()
    }

    fun microScroll() {
        Log.d("AkiFramework", "[Action] microScroll()")
        val dist = random.nextInt(100) + 50
        if (random.nextBoolean()) {
            scroll(ScrollDirection.Down, dist)
        } else {
            scroll(ScrollDirection.Up, dist)
        }
    }

    suspend fun sometimes(chance: Float, block: suspend () -> Unit) {
        Log.d("AkiFramework", "[Action] sometimes(chance=$chance)")
        if (random.nextFloat() < chance) {
            block()
        }
    }

    fun stop() {
        Log.d("AkiFramework", "[Action] stop()")
        context.stop("Action requested stop")
    }

    /**
     * Thoát khỏi Action Group hiện tại ngay lập tức.
     */
    fun endAction() {
        Log.d("AkiFramework", "[Action] endAction() - Terminating current action")
        throw EndActionException()
    }

    /**
     * Chọn ngẫu nhiên một trong các khối lệnh dựa trên trọng số (tổng trọng số nên là 1.0)
     */
    suspend fun choose(vararg possibilities: Pair<Float, suspend ActionBuilder.() -> Unit>) {
        val totalWeight = possibilities.sumOf { it.first.toDouble() }
        if (totalWeight <= 0.0) return

        val rand = random.nextDouble() * totalWeight
        var accumulated = 0.0
        
        for (i in possibilities.indices) {
            val (weight, block) = possibilities[i]
            accumulated += weight
            if (rand < accumulated || i == possibilities.size - 1) {
                Log.v("AkiFramework", "\tchoose: selected branch index $i (weight $weight)")
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
        Log.d("AkiFramework", "[Action] loop (dynamic condition) started")
        while (!context.isStopped() && condition()) {
            this.block()
        }
        Log.d("AkiFramework", "[Action] loop finished or stopped")
    }

    /**
     * Vòng lặp dựa trên số lần.
     */
    suspend fun loop(times: Int? = null, block: suspend ActionBuilder.() -> Unit) {
        var count = 0
        Log.d("AkiFramework", "[Action] loop(times=$times) started")
        loop({ times == null || count < times }) {
            Log.v("AkiFramework", "  \tloop iteration ${++count}/${times ?: "inf"}")
            this.block()
        }
    }

    /**
     * Lấy số ngẫu nhiên từ 1 đến [max]
     */
    fun random(max: Int): Int = if (max > 0) random.nextInt(max) + 1 else 1

    /**
     * Lấy số ngẫu nhiên trong khoảng [min] đến [max]
     */
    fun random(min: Int = 1, max: Int): Int = if (max > min) random.nextInt(max - min + 1) + min else min

    /**
     * Lấy số ngẫu nhiên Long trong khoảng [min] đến [max]
     */
    fun random(min: Long = 1L, max: Long): Long = if (max > min) min + (random.nextLong().run { if (this < 0) -this else this } % (max - min + 1)) else min
}
