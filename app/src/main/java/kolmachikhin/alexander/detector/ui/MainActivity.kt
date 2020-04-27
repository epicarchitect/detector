package kolmachikhin.alexander.detector.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kolmachikhin.alexander.detector.R

class MainActivity : AppCompatActivity() {

    val RC_PERMISSIONS = 10
    val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val permissionsListeners: ArrayList<() -> Unit> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        instance = this
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)
        addFragment(MainFragment(), false)
    }

    fun addFragment(f: Fragment, addToBackStack: Boolean = true) {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragmentContainer, f)
            setCustomAnimations(R.animator.show, R.animator.hide)
            if (addToBackStack)
                addToBackStack(null)
            commit()
        }
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
        permissionsListeners.forEach { it() }
        permissionsListeners.clear()
    }

    fun allPermissionsGranted() = PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {

        var instance: MainActivity? = null

    }

}
