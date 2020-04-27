package kolmachikhin.alexander.detector.ui.anylise

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import kolmachikhin.alexander.detector.R
import kolmachikhin.alexander.detector.analyse.Answer
import kolmachikhin.alexander.detector.analyse.Contour
import kolmachikhin.alexander.detector.analyse.bitmap.BitmapAnalyse
import kotlinx.android.synthetic.main.analyse_fragment.*
import kolmachikhin.alexander.detector.ui.base.BaseFragment
import kolmachikhin.alexander.detector.ui.extentions.dp
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random


class AnalyseFragment(override val layout: Int = R.layout.analyse_fragment) : BaseFragment() {

    val finderFragment = FinderFragment()
    lateinit var image: ImageView

    var cropWidth = 0
    var cropHeight = 0

    var clicked1 = false
    var clicked2 = false
    var clicked3 = false
    var clicked4 = false
    lateinit var bitmap: Bitmap

    var onDoneListener: (ArrayList<Answer>) -> Unit = {}

    fun onDone(l: (ArrayList<Answer>) -> Unit) {
        onDoneListener = l
    }

    var leftSquares = ArrayList<Contour>()
    var rightSquares = ArrayList<Contour>()

    val random = Random(System.currentTimeMillis())

    fun color() = Color.rgb(random.nextInt(180, 255), random.nextInt(180, 255), random.nextInt(180, 255))

    var scale = 4

    @ExperimentalStdlibApi
    @SuppressLint("SetTextI18n")
    override fun start() {
        scale = resources.displayMetrics.widthPixels / 120
        image = ImageView(activity)

        replaceChild(R.id.container, finderFragment)
        finderFragment.onReady {
            cropWidth = finderFragment.cropWidth / scale
            cropHeight = finderFragment.cropHeight / scale
        }

        buttonAnalyse.setOnClickListener {
            if (!clicked1) {
                clicked1 = true
                step1()
            } else if (!clicked2) {
                clicked2 = true
                step2()
            } else if (!clicked3) {
                clicked3 = true
                step3()
            } else if (!clicked4) {
                clicked4 = true
                step4()
            }
        }
    }

    fun step1() {
        finderFragment.remove()
        container.addView(image)

        bitmap = finderFragment.camera.bitmap

        val dialog = ProgressDialog.show(activity, "Analyse", "Prepare image", false, false)

        Thread {
            bitmap = BitmapAnalyse.getPreparedBitmapToAnalyse(bitmap, scale)
            runOnUi {
                image.setImageBitmap(bitmap)
                dialog.cancel()
            }
        }.start()
    }

    @ExperimentalStdlibApi
    @SuppressLint("SetTextI18n")
    fun step2() {
        val dialog = ProgressDialog.show(activity, "Analyse", "Find squares in target zones", false, false)
        Thread {
            val leftBitmap = Bitmap.createBitmap(bitmap, 0, 0, cropWidth, cropHeight)

            val offsetRightBitmap = bitmap.width - cropWidth
            val rightBitmap = Bitmap.createBitmap(bitmap, offsetRightBitmap, 0, cropWidth, cropHeight)

            val leftContours = BitmapAnalyse.scanContours(leftBitmap)
            val rightContours = BitmapAnalyse.scanContours(rightBitmap)

            leftBitmap.recycle()
            rightBitmap.recycle()

            /** =========================================== */

            leftContours.forEach {
                if (BitmapAnalyse.isMaybeSquare(it)) {
                    leftSquares.add(it)
                }
            }

            rightContours.forEach {
                it.offsetX(offsetRightBitmap)
                if (BitmapAnalyse.isMaybeSquare(it)) {
                    rightSquares.add(it)
                }
            }

            val leftAverageMinX = leftSquares.map { it.minX() }.average()
            val leftAverageWidth = leftSquares.map { it.width() }.average()

            val rightAverageMaxX = rightSquares.map { it.maxX() }.average()
            val rightAverageWidth = rightSquares.map { it.width() }.average()

            leftSquares = leftSquares.filter { it.minX() < leftAverageMinX + leftAverageWidth / 2 } as ArrayList<Contour>
            rightSquares = rightSquares.filter { it.maxX() > rightAverageMaxX - rightAverageWidth / 2 } as ArrayList<Contour>

            leftSquares.sortWith(
                compareBy(
                    { 100 - abs(it.width() - it.height()) },
                    { BitmapAnalyse.getRectPercent(it) },
                    { it.minX() }
                )
            )

            rightSquares.sortWith(
                compareBy(
                    { 100 - abs(it.width() - it.height()) },
                    { BitmapAnalyse.getRectPercent(it) },
                    { it.maxX() }
                )
            )

            while (leftSquares.size > 5) {
                leftSquares.removeFirst()
            }

            while (rightSquares.size > 5) {
                rightSquares.removeFirst()
            }

            leftSquares.forEach { contour ->
                contour.forEach { point ->
                    kotlin.runCatching {
                        bitmap.setPixel(point.x, point.y, Color.GREEN)
                    }
                }
            }

            rightSquares.forEach { contour ->
                contour.forEach { point ->
                    kotlin.runCatching {
                        bitmap.setPixel(point.x, point.y, Color.GREEN)
                    }
                }
            }

            runOnUi {
                image.setImageBitmap(bitmap)
                dialog.cancel()
            }
        }.start()
    }

    fun step3() {
        var minX = bitmap.width
        var maxX = 0
        var minY = bitmap.height
        var maxY = 0

        leftSquares.forEach {
            if (it.minX() < minX) {
                minX = it.minX()
            }

            if (it.minY() < minY) {
                minY = it.minY()
            }

            if (it.maxY() > maxY) {
                maxY = it.maxY()
            }
        }

        rightSquares.forEach {
            if (it.maxX() > maxX) {
                maxX = it.maxX()
            }

            if (it.minY() < minY) {
                minY = it.minY()
            }

            if (it.maxY() > maxY) {
                maxY = it.maxY()
            }
        }

        leftSquares.forEach {
            it.offsetX(-minX)
            it.offsetY(-minY)
        }

        rightSquares.forEach {
            it.offsetX(-minX)
            it.offsetY(-minY)
        }

        bitmap = Bitmap.createBitmap(bitmap, minX, minY, maxX - minX + 1, maxY - minY + 1)
        image.setImageBitmap(bitmap)
    }


    fun getAnswers(row: Int): ArrayList<Answer> {
        val leftSquare0 = leftSquares[row]
        val leftSquare1 = leftSquares[row + 1]

        val rightSquare0 = rightSquares[row]
        val rightSquare1 = rightSquares[row + 1]

        val minX = min(leftSquare0.minX(), leftSquare1.minX()) + max(leftSquare0.width(), leftSquare1.width())
        val maxX = max(rightSquare0.maxX(), rightSquare1.maxX()) - max(rightSquare0.width(), rightSquare1.width())
        val minY = min(leftSquare0.minY(), rightSquare0.minY()) + max(leftSquare0.height(), rightSquare0.height())
        val maxY = max(leftSquare1.maxY(), rightSquare1.maxY()) - max(leftSquare1.height(), rightSquare1.height())

        val angle = BitmapAnalyse.getAngleBetweenPoints(Point(minX, leftSquare0.maxY()), Point(maxX, rightSquare0.maxY()))
        val bitmap = BitmapAnalyse.getRotatedBitmap(Bitmap.createBitmap(bitmap, minX, minY, maxX - minX + 1, maxY - minY + 1), -angle)

        val cellWidth = (bitmap.width.toFloat() - 1) / 14
        val cellHeight = (bitmap.height.toFloat() - 1) / 4

        for (x in 0..14) {
            for (y in 0..4) {
                try {
                    bitmap.setPixel((x * cellWidth).roundToInt(), (y * cellHeight).roundToInt(), Color.MAGENTA)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        val answers = ArrayList<Answer>()

        for (x in 0 until 14) {
            val startX = (x * cellWidth).roundToInt()
            val width = (startX + cellWidth).roundToInt() - startX

            var maxPercent = 1.0
            var maxPercentPosition = -1

            for (y in 0 until 4) {
                val startY = (y * cellHeight).roundToInt()
                val height = (startY + cellHeight).roundToInt() - startY

                val squareBitmap = Bitmap.createBitmap(bitmap, startX, startY, width, height)

                val blackPercent = BitmapAnalyse.getBlackPixelsPercent(squareBitmap)

                if (blackPercent > maxPercent) {
                    maxPercent = blackPercent
                    maxPercentPosition = y
                }
            }

            answers.add(Answer[maxPercentPosition])
        }

        return answers
    }

    fun step4() {
        leftSquares.sortBy { it.minY() }
        rightSquares.sortBy { it.minY() }

        val allAnswers = ArrayList<Answer>()

        for (row in 0..4) {
            try {
                allAnswers.addAll(getAnswers(row))
            } catch (e: Exception) {}
        }

        bitmap.recycle()
        onDoneListener(allAnswers)
    }

}