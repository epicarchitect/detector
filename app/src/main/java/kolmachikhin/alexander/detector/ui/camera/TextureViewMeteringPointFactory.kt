package kolmachikhin.alexander.detector.ui.camera

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.graphics.PointF
import android.view.TextureView
import androidx.camera.core.MeteringPointFactory

@SuppressLint("RestrictedApi")
class TextureViewMeteringPointFactory(private val textureView: TextureView) : MeteringPointFactory() {

    private fun glMatrixToGraphicsMatrix(glMatrix: FloatArray): Matrix? {
        val convert = FloatArray(9)
        convert[0] = glMatrix[0]
        convert[1] = glMatrix[4]
        convert[2] = glMatrix[12]
        convert[3] = glMatrix[1]
        convert[4] = glMatrix[5]
        convert[5] = glMatrix[13]
        convert[6] = glMatrix[3]
        convert[7] = glMatrix[7]
        convert[8] = glMatrix[15]
        val graphicsMatrix = Matrix()
        graphicsMatrix.setValues(convert)
        return graphicsMatrix
    }

    override fun translatePoint(x: Float, y: Float): PointF {
        val transform = Matrix()
        textureView.getTransform(transform)

        // applying reverse of TextureView#getTransform

        // applying reverse of TextureView#getTransform
        val inverse = Matrix()
        transform.invert(inverse)
        val pt = floatArrayOf(x, y)
        inverse.mapPoints(pt)

        // get SurfaceTexture#getTransformMatrix

        // get SurfaceTexture#getTransformMatrix
        val surfaceTextureMat = FloatArray(16)
        textureView.surfaceTexture.getTransformMatrix(surfaceTextureMat)

        // convert SurfaceTexture#getTransformMatrix(4x4 column major 3D matrix) to
        // android.graphics.Matrix(3x3 row major 2D matrix)

        // convert SurfaceTexture#getTransformMatrix(4x4 column major 3D matrix) to
        // android.graphics.Matrix(3x3 row major 2D matrix)
        val surfaceTextureTransform =
            glMatrixToGraphicsMatrix(surfaceTextureMat)

        val pt2 = FloatArray(2)
        // convert to texture coordinates first.
        // convert to texture coordinates first.
        pt2[0] = pt[0] / textureView.width
        pt2[1] = (textureView.height - pt[1]) / textureView.height
        surfaceTextureTransform!!.mapPoints(pt2)

        return PointF(pt2[0], pt2[1])
    }

}