package mini

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import kolmachikhin.alexander.detector.R
import mini.masters.DisplayMaster
import kotlin.math.roundToInt

abstract class SuperActivity : AppCompatActivity() {

    private var displayMaster: DisplayMaster? = null
    private var container: FrameLayout? = null
        @SuppressLint("ResourceType")
        get() {
            if (field == null)
                field = FrameLayout(this)
            field?.id = 100000
            field?.layoutParams?.height = FrameLayout.LayoutParams.MATCH_PARENT
            field?.layoutParams?.width = FrameLayout.LayoutParams.MATCH_PARENT
            return field
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(container)
        initMasters()
        start()
    }

    abstract fun start()

    private fun initMasters() {
        displayMaster = DisplayMaster(this)
    }

    fun openAsDialog(f: SuperFragment) {
        val d = FragmentAsDialog()
        d.fragment = f
        d.show(supportFragmentManager, "")
    }

    fun open(f: SuperFragment) = container?.let { supportFragmentManager.beginTransaction().replace(it.id, f).commit() }

    /** DISPLAY MASTER DELEGATE */

    fun dp(dp: Int) = if (displayMaster == null) 0 else displayMaster!!.dp(dp).roundToInt()

    fun hideStatusBar() = displayMaster?.hideStatusBar()

}
