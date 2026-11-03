package com.aki.akiwarmup.core.human

import android.graphics.Point
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import kotlinx.coroutines.delay
import java.util.Random
import kotlin.math.pow

class HumanBehaviorEngine(private val random: Random = Random()) {

    suspend fun gaussianDelay(meanMs: Long, sigmaMs: Long = meanMs / 5) {
        val g = random.nextGaussian()
        val delayTime = (g * sigmaMs + meanMs).toLong().coerceIn((meanMs * 0.5).toLong(), (meanMs * 2.0).toLong())
        delay(delayTime)
    }

    suspend fun breathingPause() {
        gaussianDelay(300, 100)
    }

    fun humanSwipe(device: UiDevice, from: Point, to: Point, steps: Int = 12) {
        val points = mutableListOf<Point>()
        
        // Mô phỏng cung ngón tay cái (Thumb Arc):
        // 70% ngón cái tay phải quét cong sang trái (arcSign = -1), 30% ngón cái tay trái quét cong sang phải (arcSign = 1)
        val arcSign = if (random.nextFloat() < 0.70f) -1 else 1
        
        // Độ võng cung sinh học: 35px - 65px ở 1/3 chặng đầu, 20px - 45px ở 2/3 chặng sau
        val offset1 = arcSign * (random.nextInt(30) + 35)
        val offset2 = arcSign * (random.nextInt(25) + 20)

        val cp1 = Point(
            from.x + offset1,
            from.y + (to.y - from.y) / 3
        )
        val cp2 = Point(
            to.x + offset2,
            from.y + 2 * (to.y - from.y) / 3
        )

        for (i in 0..steps) {
            val t = i.toFloat() / steps
            var x = (1 - t).pow(3) * from.x + 3 * (1 - t).pow(2) * t * cp1.x + 3 * (1 - t) * t.pow(2) * cp2.x + t.pow(3) * to.x
            var y = (1 - t).pow(3) * from.y + 3 * (1 - t).pow(2) * t * cp1.y + 3 * (1 - t) * t.pow(2) * cp2.y + t.pow(3) * to.y
            
            // Bổ sung rung vi mô (micro-perturbation ±2px) cho các điểm trung gian (trừ điểm đầu và điểm cuối)
            if (i in 1 until steps) {
                x += (random.nextInt(5) - 2)
                y += (random.nextInt(5) - 2)
            }
            
            points.add(Point(x.toInt(), y.toInt()))
        }

        // Đảm bảo bước di chuyển đầu tiên (points[1]) bứt phá ngay qua ngưỡng touch_slop (> 35px)
        // để Android View hủy ngay lập tức trạng thái PRESSED và không nhận nhầm thành Tap
        if (points.size > 1 && kotlin.math.abs(points[1].y - points[0].y) < 35) {
            val direction = if (to.y < from.y) -1 else 1
            points[1] = Point(points[1].x, points[0].y + direction * 35)
        }

        // segmentSteps = 2 (tổng thời gian ~120ms): vượt ngưỡng TapTimeout (100ms) của Android
        // giúp hệ thống nhận diện chắc chắn đây là cử chỉ FLING lướt trang của ViewPager2, không nhận nhầm thành Tap tạm dừng
        device.swipe(points.toTypedArray(), 2)
    }

    suspend fun humanType(field: UiObject2, text: String) {
        runCatching { field.click() }
        gaussianDelay(100, 30)

        val isLongText = text.length > 20
        var i = 0
        while (i < text.length) {
            // Gõ theo cụm ký tự thích ứng (văn bản dài gõ cụm lớn hơn để giảm nghẽn IPC setText)
            val chunkSize = if (isLongText) {
                random.nextInt(4) + 3 // 3..6 ký tự
            } else if (random.nextFloat() < 0.4f) {
                random.nextInt(3) + 1 // 1..3 ký tự
            } else {
                1
            }
            val nextIndex = (i + chunkSize).coerceAtMost(text.length)

            val updated = runCatching {
                field.text = text.substring(0, nextIndex)
                true
            }.getOrDefault(false)
            if (!updated) break
            i = nextIndex

            // Delay cực ngắn giữa các cụm
            delay(random.nextInt(10) + 10L)

            // Tỉ lệ lỗi cực thấp (1%)
            if (i < text.length && random.nextFloat() < 0.01f) {
                val wrongChar = ('a'.code + random.nextInt(26)).toChar()
                runCatching { field.text = text.substring(0, i) + wrongChar }
                delay(30)
                runCatching { field.text = text.substring(0, i) }
                delay(50)
            }
        }
    }
    
    fun getScatterPoint(center: Point, scatterPx: Int): Point {
        if (scatterPx <= 0) return center
        return Point(
            center.x + random.nextInt(scatterPx * 2) - scatterPx,
            center.y + random.nextInt(scatterPx * 2) - scatterPx
        )
    }
}
