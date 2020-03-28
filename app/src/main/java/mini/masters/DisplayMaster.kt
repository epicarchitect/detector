package mini.masters

import android.view.WindowManager
import mini.SuperActivity

class DisplayMaster(private val activity: SuperActivity) {

    fun hideStatusBar() = activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

    fun dp(dp: Int) = activity.resources.displayMetrics.density * dp

}