package kolmachikhin.alexander.detector.analyse

import android.graphics.Point
import com.google.gson.Gson
import kolmachikhin.alexander.detector.analyse.bitmap.BitmapAnalyse
import java.nio.file.attribute.AclEntry.newBuilder
import kotlin.math.abs

class Contour {

    val list = ArrayList<Point>()

    fun width() = maxX() - minX() + 1
    fun height() = maxY() - minY() + 1

    fun set(p: Point) {
        var i = 0
        list.forEach {
            if (p.x == it.x && p.y == it.y) {
                list[i] = p
                return
            }
            i++
        }
        list.add(p)
    }

    operator fun get(x: Int, y: Int): Point? {
        list.forEach {
            if (x == it.x && y == it.y) {
                return it
            }
        }
        return null
    }

    fun offsetX(offset: Int) {
        forEach { it.x += offset }
    }

    fun offsetY(offset: Int) {
        forEach { it.y += offset }
    }

    fun isExist(x: Int, y: Int): Boolean {
        list.forEach {
            if (x == it.x && y == it.y) {
                return true
            }
        }
        return false
    }

    val isEmpty get() = list.isEmpty()

    val size get() = list.size

    fun isInto(x: Int, y: Int): Boolean {
        var countFromLeft = 0 // сколько x был больше it.x
        var countFromRight = 0 // сколько x был меньше it.x
        var countFromTop = 0 // сколько y был больше it.y
        var countFromBottom = 0 // сколько y был меньше it.y

        /***
         * Когда все count будут > 0 значит isInto
         */

        list.forEach {
            if (x > it.x) countFromLeft++
            if (x < it.x) countFromRight++
            if (y > it.y) countFromTop++
            if (y < it.y) countFromBottom++

            if (countFromLeft > 0 && countFromRight > 0 && countFromTop > 0 && countFromBottom > 0) {
                return true
            }
        }

        return false
    }

    fun isFirstNearWithLast(): Boolean {
        val first = list.first()
        val last = list.last()

        var isNear = false

        BitmapAnalyse.pointsAround(first) {
            if (it == last) {
                isNear = true
                return@pointsAround
            }
        }

        return isNear
    }

    fun forEach(l: ((point: Point) -> Unit)) {
        list.forEach { l.invoke(it) }
    }

    fun maxX(): Int {
        var max = 0
        forEach {
            if (it.x > max) {
                max = it.x
            }
        }
        return max
    }

    fun maxY(): Int {
        var max = 0
        forEach {
            if (it.y > max) {
                max = it.y
            }
        }
        return max
    }

    fun minX(): Int {
        var min = Int.MAX_VALUE
        forEach {
            if (it.x < min) {
                min = it.x
            }
        }
        if (min == Int.MAX_VALUE) min = 0
        return min
    }

    fun minY(): Int {
        var min = Int.MAX_VALUE
        forEach {
            if (it.y < min) {
                min = it.y
            }
        }
        if (min == Int.MAX_VALUE) min = 0
        return min
    }

}