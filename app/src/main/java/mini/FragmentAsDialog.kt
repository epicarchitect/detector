package mini

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import kolmachikhin.alexander.detector.R

class FragmentAsDialog : DialogFragment() {

    private var isReady: Boolean = false
    private val onReadyListeners: ArrayList<() -> Unit> = ArrayList()

    private var container: FrameLayout? = null
        @SuppressLint("ResourceType")
        get() {
            if (field == null)
                field = activity?.let { FrameLayout(it) }

            field?.id = 100000
            field?.layoutParams?.height = FrameLayout.LayoutParams.MATCH_PARENT
            field?.layoutParams?.width = FrameLayout.LayoutParams.MATCH_PARENT

            return field
        }

    private fun onReady(l: () -> Unit) {
        if (isReady) l.invoke()
        else onReadyListeners.add(l)
    }

    private fun ready() {
        isReady = true
        onReadyListeners.forEach { it.invoke() }
        onReadyListeners.clear()
    }

    var fragment: SuperFragment? = null
        set(value) {
            field = value

            fragment?.onRemove {
                dismiss()
            }

            onReady {
                container?.let {
                    childFragmentManager
                        .beginTransaction()
                        .replace(it.id, fragment as Fragment)
                        .commit()
                }
            }
        }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? = container

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ready()
    }

    override fun onStop() {
        super.onStop()
        isReady = false
    }

}