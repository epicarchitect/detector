package kolmachikhin.alexander.detector.ui.extentions

import android.widget.EditText

fun EditText.text() = text(true)

fun EditText.text(withTrim: Boolean) = if (withTrim) text.trim().toString() else text.toString()

fun EditText.double(): Double {
    var str = text.toString().replace(",", ".")
    try {
        str = str.toDouble().toString() + ""
    } catch (e: Exception) {
        e.printStackTrace()
    }
    if (str == "") setText("0")
    var value = 0.0
    try {
        value = str.toDouble()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return value
}