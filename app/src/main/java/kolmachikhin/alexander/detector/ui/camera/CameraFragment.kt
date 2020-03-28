package kolmachikhin.alexander.detector.ui.camera

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Size
import android.view.Surface
import android.view.ViewGroup
import androidx.camera.core.*
import kolmachikhin.alexander.detector.MainActivity
import kolmachikhin.alexander.detector.R
import kotlinx.android.synthetic.main.camera_fragment.*
import mini.SuperFragment

@SuppressLint("RestrictedApi")
class CameraFragment(override val layout: Int = R.layout.camera_fragment) : SuperFragment() {

    val cameraWidth get() = resources.displayMetrics.widthPixels
    val cameraHeight get() = resources.displayMetrics.heightPixels

    private var updateThreadRunning = true

    private val bitmapUpdateThread = Thread {
        while (updateThreadRunning) {
            try {
                bitmap = textureView.bitmap
                Thread.sleep(10)
            } catch (e: Exception) {
                e.printStackTrace()
                updateThreadRunning = false
            }
        }
    }

    private var bitmapUpdateListener: ((Bitmap) -> Unit)? = null
    var bitmap: Bitmap? = null
        set(value) {
            field = value
            if (value != null) {
                bitmapUpdateListener?.invoke(value)
            }
        }

    override fun start() {
        textureView.layoutParams.width = cameraWidth
        textureView.layoutParams.height = cameraHeight
        textureView.invalidate()
        setUpTapToFocus()
    }

    fun startCamera() {
        onReady {
            textureView.post {
                (activity as MainActivity).ifAllPermissionsGranted {
                    val previewConfig = PreviewConfig.Builder()
                        .setLensFacing(CameraX.LensFacing.BACK)
                        .setTargetResolution(Size(cameraWidth, cameraHeight))
                        .build()

                    val preview = Preview(previewConfig)

                    preview.setOnPreviewOutputUpdateListener {
                        val parent = textureView.parent as ViewGroup
                        parent.removeView(textureView)
                        parent.addView(textureView, 0)
                        textureView.surfaceTexture = it.surfaceTexture
                        updateTransform()
                    }


                    CameraX.bindToLifecycle(this, preview)

                    bitmapUpdateThread.start()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        updateThreadRunning = false
    }

    fun setUpTapToFocus() {
        onReady {
            textureView.setOnTouchListener { _, event ->
                focus(event.x, event.y)
                return@setOnTouchListener true
            }
        }
    }

    fun focus(x: Float, y:Float) {
        val cameraControl = CameraX.getCameraControl(CameraX.LensFacing.BACK)
        val point = TextureViewMeteringPointFactory(textureView).createPoint(x, y)
        val action = FocusMeteringAction.Builder.from(point).build()
        cameraControl.startFocusAndMetering(action)
    }

    fun observe(l: (Bitmap?) -> Unit) {
        bitmapUpdateListener = l
    }

    fun removeObserver() {
        bitmapUpdateListener = null
    }

    private fun updateTransform() {
        val matrix = Matrix()

        val centerX = textureView.width / 2f
        val centerY = textureView.height / 2f

        val rotationDegrees = when (textureView.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }

        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        textureView.setTransform(matrix)
    }

}