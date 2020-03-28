package kolmachikhin.alexander.detector.analyse.bitmap

import android.graphics.Bitmap
import android.graphics.Point
import android.util.Log
import kolmachikhin.alexander.detector.analyse.Matrix
import kolmachikhin.alexander.detector.analyse.Contour
import kotlin.math.*

class BitmapAnalyse {

    companion object {

        fun pointsAround(point: Point, radius: Int = 1, l: ((point: Point) -> Unit)) {
            pointsAround(point.x, point.y, radius, l)
        }

        fun pointsAround(x: Int, y: Int, radius: Int = 1, l: ((point: Point) -> Unit)) {
            /**
             * p1 p2 p3
             * p8 xy p4
             * p7 p6 p5
             * */
            for (i in 1..radius) {
                val p1 = Point(x - i, y - i)
                val p2 = Point(x - 0, y - i)
                val p3 = Point(x + i, y - i)
                val p4 = Point(x + i, y - 0)
                val p5 = Point(x + i, y + i)
                val p6 = Point(x - 0, y + i)
                val p7 = Point(x - i, y + i)
                val p8 = Point(x - i, y - 0)


                l.invoke(p2)
                l.invoke(p4)
                l.invoke(p6)
                l.invoke(p8)

                l.invoke(p1)
                l.invoke(p3)
                l.invoke(p5)
                l.invoke(p7)
            }
        }

        fun buildRectContour(startX: Int, startY: Int, width: Int, height: Int, strokeWidth: Int = 1): Contour {
            val rect = Contour()

            for (x in startX until startX + width) {
                for (i in 0 until strokeWidth) {
                    rect.set(Point(x, startY + i))
                    rect.set(Point(x, startY - i + height - 1))
                }
            }

            for (y in startY until startY + height) {
                for (i in 0 until strokeWidth) {
                    rect.set(Point(startX + i, y))
                    rect.set(Point(startX - i + width - 1, y))
                }
            }

            return rect
        }

        fun isMaybeSquare(contour: Contour): Boolean {
            val width = contour.width()
            val height = contour.height()
            return contour.size in 12..50
                    && contour.isFirstNearWithLast()
                    && getRectPercent(contour) >= 80
                    && abs(width - height) <= 2
        }

        fun getSquarePercent(contour: Contour, strokeWidth: Int = 1): Float {
            var maxPercent = 0F

            for (i in 0..180) {
                val rotatedContour = getRotatedContour(contour, i.toFloat())
                val size = min(rotatedContour.width(), rotatedContour.height())
                val rect = buildRectContour(rotatedContour.minX(), rotatedContour.minY(), size, size, strokeWidth)

                var countExists = 0
                rotatedContour.forEach {
                    if (rect.isExist(it.x, it.y)) {
                        countExists++
                    }
                }

                val percent = countExists.toFloat() * 100 / rotatedContour.size

                if (percent > maxPercent) {
                    maxPercent = percent
                }
            }

            return maxPercent
        }

        fun getRectPercent(contour: Contour, strokeWidth: Int = 1): Float {
            var maxPercent = 0F

            for (i in 0..180) {
                val rotatedContour = getRotatedContour(contour, i.toFloat())
                val rect = buildRectContour(rotatedContour.minX(), rotatedContour.minY(), rotatedContour.width(), rotatedContour.height(), strokeWidth)
                var countExists = 0

                rotatedContour.forEach {
                    if (rect.isExist(it.x, it.y)) {
                        countExists++
                    }
                }

                val percent = countExists.toFloat() * 100 / rotatedContour.size

                if (percent > maxPercent) {
                    maxPercent = percent
                }
            }

            return maxPercent
        }

        fun degreesToRadians(degrees: Float): Float {
            return degrees * (Math.PI.toFloat() / 180)
        }

        fun getRotatedPoint(x: Int, y: Int, degrees: Float): Point {
            val radians = degreesToRadians(degrees)
            val newX = x * cos(radians) - y * sin(radians)
            val newY = x * sin(radians) + y * cos(radians);
            return Point((newX).roundToInt(), (newY).roundToInt())
        }

        fun getRotatedContour(contour: Contour, degrees: Float): Contour {
            val offsetX = contour.maxX() - contour.width() / 2
            val offsetY = contour.maxY() - contour.height() / 2

            fun rotatedPoint(point: Point): Point {
                val x = point.x - offsetX
                val y = point.y - offsetY
                val rotatedPoint = getRotatedPoint(x, y, degrees)
                return Point(rotatedPoint.x + offsetX, rotatedPoint.y + offsetY)
            }

            val rotatedContour = Contour()
            contour.forEach {
                rotatedContour.set(rotatedPoint(it))
            }
            return rotatedContour
        }

        fun scanContours(bitmap: Bitmap): ArrayList<Contour> {
            val matrix = scanContoursMatrix(bitmap)
            val contours = ArrayList<Contour>()
            val usedPoints = Matrix<Point>()

            fun isUsed(x: Int, y: Int) = usedPoints.isExist(x, y)

            fun buildContour(startPoint: Point, parents: ArrayList<Contour> = ArrayList()): Contour {
                val contour = Contour()
                contour.set(startPoint)
                usedPoints[startPoint.x, startPoint.y] = startPoint

                fun isExistInParents(x: Int, y: Int): Boolean {
                    parents.forEach {
                        if (it.isExist(x, y))
                            return true
                    }
                    return false
                }

                do {
                    val lastPoint = contour.list.last()

                    val pointCandidates = ArrayList<Point>()
                    pointsAround(lastPoint) {
                        if (!contour.isExist(it.x, it.y)
                            && !isExistInParents(it.x, it.y)
                            && matrix.isExist(it.x, it.y)) {
                            pointCandidates.add(it)
                        }
                    }

                    if (pointCandidates.size == 0) return contour


                    if (pointCandidates.size >= 2) {
                        var maxContourBranch = Contour()

                        pointCandidates.forEach {
                            parents.add(contour)
                            val contourBranch = buildContour(it, parents)
                            if (contourBranch.size > maxContourBranch.size) {
                                maxContourBranch = contourBranch
                            }
                        }

                        maxContourBranch.forEach { contour.set(it) }

                        return contour

                    } else {
                        val point = pointCandidates[0]
                        contour.set(point)
                        usedPoints[point.x, point.y] = point
                    }

                } while (true)
            }

            matrix.forEach {
                if (!isUsed(it.x, it.y)) {
                    val contour = buildContour(it)
                    contours.add(contour)
                }
            }

            return contours
        }

        fun scanContoursMatrix(bitmap: Bitmap): Matrix<Point> {
            val matrix = Matrix<Point>()

            fun scanVertical() {
                for (x in 0 until bitmap.width) {
                    var lastIsBlack = false
                    for (y in 0 until bitmap.height) {
                        lastIsBlack = if (bitmap.isBlack(x, y)) {
                            if (!lastIsBlack) {
                                matrix[x, y] = Point(x, y)
                            }
                            true
                        } else {
                            if (lastIsBlack) {
                                matrix[x, y - 1] = Point(x, y -1)
                            }
                            false
                        }

                        if (y == bitmap.height - 1 && lastIsBlack) {
                            matrix[x, y] = Point(x, y)
                        }
                    }
                }
            }

            fun scanHorizontal() {
                for (y in 0 until bitmap.height) {
                    var lastIsBlack = false
                    for (x in 0 until bitmap.width) {
                        lastIsBlack = if (bitmap.isBlack(x, y)) {
                            if (!lastIsBlack) {
                                matrix[x, y] = Point(x, y)
                            }
                            true
                        } else {
                            if (lastIsBlack) {
                                matrix[x - 1, y] = Point(x - 1, y)
                            }
                            false
                        }

                        if (x == bitmap.width - 1 && lastIsBlack) {
                            matrix[x, y] = Point(x, y)
                        }
                    }
                }
            }

            scanVertical()
            scanHorizontal()

            return matrix

        }

        // copied from github...
        fun getBlurBitmap(sentBitmap: Bitmap, radius: Int): Bitmap {

            val bitmap = sentBitmap.copy(sentBitmap.config, true)

            if (radius < 1) {
                throw IllegalArgumentException("Needs a positive radius")
            }

            val w = bitmap.width
            val h = bitmap.height

            val pix = IntArray(w * h)
            bitmap.getPixels(pix, 0, w, 0, 0, w, h)

            val wm = w - 1
            val hm = h - 1
            val wh = w * h
            val div = radius + radius + 1

            val r = IntArray(wh)
            val g = IntArray(wh)
            val b = IntArray(wh)
            var rsum: Int
            var gsum: Int
            var bsum: Int
            var x: Int
            var y: Int
            var i: Int
            var p: Int
            var yp: Int
            var yi: Int
            var yw: Int
            val vmin = IntArray(max(w, h))

            var divsum = div + 1 shr 1
            divsum *= divsum
            val dv = IntArray(256 * divsum)
            i = 0
            while (i < 256 * divsum) {
                dv[i] = i / divsum
                i++
            }

            yi = 0
            yw = yi

            val stack = Array(div) { IntArray(3) }
            var stackpointer: Int
            var stackstart: Int
            var sir: IntArray
            var rbs: Int
            val r1 = radius + 1
            var routsum: Int
            var goutsum: Int
            var boutsum: Int
            var rinsum: Int
            var ginsum: Int
            var binsum: Int

            y = 0
            while (y < h) {
                bsum = 0
                gsum = bsum
                rsum = gsum
                boutsum = rsum
                goutsum = boutsum
                routsum = goutsum
                binsum = routsum
                ginsum = binsum
                rinsum = ginsum
                i = -radius
                while (i <= radius) {
                    p = pix[yi + min(wm, max(i, 0))]
                    sir = stack[i + radius]
                    sir[0] = p and 0xff0000 shr 16
                    sir[1] = p and 0x00ff00 shr 8
                    sir[2] = p and 0x0000ff
                    rbs = r1 - abs(i)
                    rsum += sir[0] * rbs
                    gsum += sir[1] * rbs
                    bsum += sir[2] * rbs
                    if (i > 0) {
                        rinsum += sir[0]
                        ginsum += sir[1]
                        binsum += sir[2]
                    } else {
                        routsum += sir[0]
                        goutsum += sir[1]
                        boutsum += sir[2]
                    }
                    i++
                }
                stackpointer = radius

                x = 0
                while (x < w) {
                    r[yi] = dv[rsum]
                    g[yi] = dv[gsum]
                    b[yi] = dv[bsum]

                    rsum -= routsum
                    gsum -= goutsum
                    bsum -= boutsum

                    stackstart = stackpointer - radius + div
                    sir = stack[stackstart % div]

                    routsum -= sir[0]
                    goutsum -= sir[1]
                    boutsum -= sir[2]

                    if (y == 0) {
                        vmin[x] = min(x + radius + 1, wm)
                    }
                    p = pix[yw + vmin[x]]

                    sir[0] = p and 0xff0000 shr 16
                    sir[1] = p and 0x00ff00 shr 8
                    sir[2] = p and 0x0000ff

                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]

                    rsum += rinsum
                    gsum += ginsum
                    bsum += binsum

                    stackpointer = (stackpointer + 1) % div
                    sir = stack[stackpointer % div]

                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]

                    rinsum -= sir[0]
                    ginsum -= sir[1]
                    binsum -= sir[2]

                    yi++
                    x++
                }
                yw += w
                y++
            }
            x = 0
            while (x < w) {
                bsum = 0
                gsum = bsum
                rsum = gsum
                boutsum = rsum
                goutsum = boutsum
                routsum = goutsum
                binsum = routsum
                ginsum = binsum
                rinsum = ginsum
                yp = -radius * w
                i = -radius
                while (i <= radius) {
                    yi = max(0, yp) + x

                    sir = stack[i + radius]

                    sir[0] = r[yi]
                    sir[1] = g[yi]
                    sir[2] = b[yi]

                    rbs = r1 - abs(i)

                    rsum += r[yi] * rbs
                    gsum += g[yi] * rbs
                    bsum += b[yi] * rbs

                    if (i > 0) {
                        rinsum += sir[0]
                        ginsum += sir[1]
                        binsum += sir[2]
                    } else {
                        routsum += sir[0]
                        goutsum += sir[1]
                        boutsum += sir[2]
                    }

                    if (i < hm) {
                        yp += w
                    }
                    i++
                }
                yi = x
                stackpointer = radius
                y = 0
                while (y < h) {
                    // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                    pix[yi] = -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]

                    rsum -= routsum
                    gsum -= goutsum
                    bsum -= boutsum

                    stackstart = stackpointer - radius + div
                    sir = stack[stackstart % div]

                    routsum -= sir[0]
                    goutsum -= sir[1]
                    boutsum -= sir[2]

                    if (x == 0) {
                        vmin[y] = min(y + r1, hm) * w
                    }
                    p = x + vmin[y]

                    sir[0] = r[p]
                    sir[1] = g[p]
                    sir[2] = b[p]

                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]

                    rsum += rinsum
                    gsum += ginsum
                    bsum += binsum

                    stackpointer = (stackpointer + 1) % div
                    sir = stack[stackpointer]

                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]

                    rinsum -= sir[0]
                    ginsum -= sir[1]
                    binsum -= sir[2]

                    yi += w
                    y++
                }
                x++
            }

            bitmap.setPixels(pix, 0, w, 0, 0, w, h)

            return bitmap
        }


        fun getAngleBetweenPoints(pointAsCenter: Point, pointForAngle: Point)
                = 180 * atan2((pointForAngle.y - pointAsCenter.y).toDouble(), (pointForAngle.x - pointAsCenter.x).toDouble()) / Math.PI

        fun getPreparedBitmapToAnalyse(b: Bitmap, scale: Int): Bitmap {
            val bitmap = Bitmap.createScaledBitmap(b, b.width / scale, b.height / scale, true)
            bitmap.toBlackWhite()
            bitmap.clean(3)
            Log.d("test", "before - width: ${b.width}, height: ${b.height}")
            Log.d("test", "after - width: ${bitmap.width}, height: ${bitmap.height}")
            return bitmap
        }
    }

}