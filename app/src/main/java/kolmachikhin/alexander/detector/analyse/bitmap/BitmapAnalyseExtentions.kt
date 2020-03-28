package kolmachikhin.alexander.detector.analyse.bitmap

import android.graphics.Bitmap
import android.graphics.Color

fun Bitmap.clean(repeatCount: Int = 1) {
    for (i in 0 until repeatCount) {
        this.forEach { x, y ->
            if (!this.isBlack(x, y)) {
                var countBlackPointsAround = 0
                BitmapAnalyse.pointsAround(x, y) {
                    if (this.isBlack(it.x, it.y)) {
                        countBlackPointsAround++
                        if (countBlackPointsAround >= 5) {
                            this.setPixel(x, y, Color.BLACK)
                            return@pointsAround
                        }
                    }
                }
            }
        }
    }

    for (i in 0 until repeatCount) {
        this.forEach { x, y ->
            if (this.isBlack(x, y)) {
                var countWhitePointsAround = 0
                BitmapAnalyse.pointsAround(x, y) {
                    if (!this.isBlack(it.x, it.y)) {
                        countWhitePointsAround++
                        if (countWhitePointsAround >= 6) {
                            this.setPixel(x, y, Color.WHITE)
                            return@pointsAround
                        }
                    }
                }
            }
        }
    }
}

fun Bitmap.forEach(l: (x: Int, y: Int) -> Unit) {
    for (x in 0 until this.width) {
        for (y in 0 until this.height) {
            l.invoke(x, y)
        }
    }
}

fun Bitmap.isBlack(x: Int, y: Int): Boolean {
    if (x < 0 || y < 0 || x >= this.width || y >= this.height)
        return false

    val pixel = this.getPixel(x, y)
    val r = Color.red(pixel)
    val g = Color.green(pixel)
    val b = Color.blue(pixel)
    return isBlack(r, g, b)
}

fun isBlack(r: Int, g: Int, b: Int) = r <= 130 && g <= 130 && b <= 255

fun Bitmap.toBlackWhite() {
    this.forEach { x, y ->
        if (this.isBlack(x, y)) {
            this.setPixel(x, y, Color.BLACK)
        } else {
            this.setPixel(x, y, Color.WHITE)
        }
    }
}