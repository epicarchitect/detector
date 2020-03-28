package kolmachikhin.alexander.detector.ui.anylise

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import kolmachikhin.alexander.detector.R
import kolmachikhin.alexander.detector.analyse.Contour
import kolmachikhin.alexander.detector.analyse.bitmap.BitmapAnalyse
import kotlinx.android.synthetic.main.analyse_fragment.*
import mini.SuperFragment
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random


class AnalyseFragment(override val layout: Int = R.layout.analyse_fragment) : SuperFragment() {

    val finderFragment = FinderFragment()
    lateinit var image: ImageView

    var cropWidth = 0
    var cropHeight = 0

    var clicked1 = false
    var clicked2 = false
    var clicked3 = false
    lateinit var bitmap: Bitmap

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
            }
        }
    }

    fun step1() {
        finderFragment.remove()
        container.addView(image)

        bitmap = finderFragment.camera.bitmap!!

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
                    { it.minX() },
                    { BitmapAnalyse.getRectPercent(it) }
                )
            )

            rightSquares.sortWith(
                compareBy(
                    { 100 - abs(it.width() - it.height()) },
                    { it.maxX() },
                    { BitmapAnalyse.getRectPercent(it) }
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

        bitmap = Bitmap.createBitmap(bitmap, minX, minY, maxX - minX + 1, maxY - minY + 1)

        image.setImageBitmap(bitmap)
    }


}