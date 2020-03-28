package kolmachikhin.alexander.detector.ui.anylise

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import kolmachikhin.alexander.detector.R
import kolmachikhin.alexander.detector.ui.camera.CameraFragment
import kolmachikhin.alexander.detector.analyse.bitmap.BitmapAnalyse
import kolmachikhin.alexander.detector.analyse.bitmap.toBlackWhite
import kotlinx.android.synthetic.main.finder_fragment.*
import mini.SuperFragment

class FinderFragment(override val layout: Int = R.layout.finder_fragment) : SuperFragment() {

    var cropWidth = 0
    var cropHeight = 0

    var countUpdates = 0
    val camera = CameraFragment()

    var onFindListener: (() -> Unit)? = null
    var onLostListener: (() -> Unit)? = null

    val scale = 4

    override fun start() {
        cropWidth = activity.dp(50)
        cropHeight = resources.displayMetrics.heightPixels

        camera.startCamera()
        setObserving()
        replaceChild(R.id.cameraContainer, camera)
    }

    @SuppressLint("SetTextI18n")
    private fun setObserving() {
        camera.observe {
            it?.let {
                val bitmap1 = Bitmap.createBitmap(it, 0, 0, cropWidth, cropHeight)
                val bitmap2 = Bitmap.createBitmap(it, resources.displayMetrics.widthPixels - cropWidth, 0, cropWidth, cropHeight)

                val croppedBitmap1 = Bitmap.createScaledBitmap(bitmap1, cropWidth / scale, cropHeight / scale, true)
                val croppedBitmap2 = Bitmap.createScaledBitmap(bitmap2, cropWidth / scale, cropHeight / scale, true)

                bitmap1.recycle()
                bitmap2.recycle()

                croppedBitmap1.toBlackWhite()
                croppedBitmap2.toBlackWhite()

                BitmapAnalyse.scanContoursMatrix(croppedBitmap1).forEach { point ->
                    croppedBitmap1.setPixel(point.x, point.y, Color.RED)
                }

                BitmapAnalyse.scanContoursMatrix(croppedBitmap2).forEach { point ->
                    croppedBitmap2.setPixel(point.x, point.y, Color.RED)
                }

                runOnUi {
                    kotlin.runCatching {
                        leftTargetFrame.setImageBitmap(croppedBitmap1)
                        rightTargetFrame.setImageBitmap(croppedBitmap2)
                    }
                }
            }
        }
    }

    override fun onStop() {
        camera.removeObserver()
        super.onStop()
    }

    fun onFind(l: () -> Unit) {
        onFindListener = l
    }

    fun onLost(l: () -> Unit) {
        onLostListener = l
    }

}