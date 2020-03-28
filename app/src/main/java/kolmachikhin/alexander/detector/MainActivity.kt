package kolmachikhin.alexander.detector

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Point
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kolmachikhin.alexander.detector.analyse.bitmap.BitmapAnalyse
import kolmachikhin.alexander.detector.ui.MainFragment
import mini.SuperActivity

class MainActivity : SuperActivity() {

    val RC_PERMISSIONS = 10
    val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    val permissionsListeners: ArrayList<() -> Unit> = ArrayList()

    override fun start() {
        hideStatusBar()
        open(MainFragment())
    }

    fun ifAllPermissionsGranted(l: () -> Unit) {
        if (allPermissionsGranted()) {
            l.invoke()
        } else {
            permissionsListeners.add(l)
            ActivityCompat.requestPermissions(this, PERMISSIONS, RC_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsListeners.forEach { it.invoke() }
        permissionsListeners.clear()
    }

    fun allPermissionsGranted() = PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


}
