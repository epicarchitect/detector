package kolmachikhin.alexander.detector.ui.extentions

import android.app.Activity
import android.util.TypedValue
import android.view.WindowManager


fun Activity.attr(res: Int): Int {
    val value = TypedValue()
    theme.resolveAttribute(res, value, true)
    return value.data
}