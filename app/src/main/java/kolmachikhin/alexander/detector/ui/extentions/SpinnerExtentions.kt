package kolmachikhin.alexander.detector.ui.extentions

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner

fun Spinner.onSelect(l: ((position: Int) -> Unit)) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            l.invoke(position)
        }
    }
}