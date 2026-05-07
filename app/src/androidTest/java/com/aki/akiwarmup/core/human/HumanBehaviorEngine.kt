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
        
        // Control points for Bezier (randomized slightly for "human" curve)
        val cp1 = Point(
            from.x + (random.nextInt(100) - 50),
            from.y + (to.y - from.y) / 3
        )
        val cp2 = Point(
            to.x + (random.nextInt(100) - 50),
            from.y + 2 * (to.y - from.y) / 3
        )

        for (i in 0..steps) {
            val t = i.toFloat() / steps
            val x = (1 - t).pow(3) * from.x + 3 * (1 - t).pow(2) * t * cp1.x + 3 * (1 - t) * t.pow(2) * cp2.x + t.pow(3) * to.x
            val y = (1 - t).pow(3) * from.y + 3 * (1 - t).pow(2) * t * cp1.y + 3 * (1 - t) * t.pow(2) * cp2.y + t.pow(3) * to.y
            points.add(Point(x.toInt(), y.toInt()))
        }

        device.swipe(points.toTypedArray(), 2)
    }

    suspend fun humanType(field: UiObject2, text: String) {
        field.click()
        gaussianDelay(100, 30)
        
        var i = 0
        while (i < text.length) {
            // Gõ theo cụm 1-3 ký tự để tăng tốc độ tối đa
            val chunkSize = if (random.nextFloat() < 0.4) (1..3).random() else 1
            val nextIndex = (i + chunkSize).coerceAtMost(text.length)
            
            field.text = text.substring(0, nextIndex)
            i = nextIndex
            
            // Delay cực ngắn giữa các cụm
            delay(random.nextInt(10) + 10L)
            
            // Tỉ lệ lỗi cực thấp (1%)
            if (random.nextFloat() < 0.03) {
                val wrongChar = ('a'..'z').random()
                field.text = text.substring(0, i) + wrongChar
                delay(30)
                field.text = text.substring(0, i)
                delay(50)
            }
        }
    }
    
    fun getScatterPoint(center: Point, scatterPx: Int): Point {
        return Point(
            center.x + random.nextInt(scatterPx * 2) - scatterPx,
            center.y + random.nextInt(scatterPx * 2) - scatterPx
        )
    }
}
