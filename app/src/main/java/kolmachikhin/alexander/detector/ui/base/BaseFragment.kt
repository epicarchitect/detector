package kolmachikhin.alexander.detector.ui.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kolmachikhin.alexander.detector.R
import kolmachikhin.alexander.detector.ui.MainActivity

abstract class BaseFragment : Fragment() {

    private var isReady = false
    private val onReadyListeners: ArrayList<() -> Unit> = ArrayList()
    private val onRemoveListeners: ArrayList<() -> Unit> = ArrayList()

    val activity get() = super.getActivity() as MainActivity

    fun onReady(l: () -> Unit) {
        if (isReady) l.invoke()
        else onReadyListeners.add(l)
    }

    fun onRemove(l: () -> Unit) {
        if (isRemoving) l.invoke()
        else onRemoveListeners.add(l)
    }

    private fun ready() {
        isReady = true
        onReadyListeners.forEach { it.invoke() }
        onReadyListeners.clear()
    }

    abstract val layout: Int
    abstract fun start()

    override fun onStart() {
        super.onStart()
        start()
        ready()
    }

    fun runOnUi(l: () -> Unit) = activity.runOnUiThread(l)

    override fun onStop() {
        super.onStop()
        isReady = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout, container,false)
    }

    fun addChild(containerId: Int, f: Fragment) {
        onReady {
            childFragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.show, R.animator.hide)
                .add(containerId, f)
                .commit()
        }
    }

    fun replaceChild(containerId: Int, f: Fragment) {
        onReady {
            childFragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.show, R.animator.hide)
                .replace(containerId, f)
                .commit()
        }
    }

    fun remove() {
        fragmentManager?.beginTransaction()
            ?.setCustomAnimations(R.animator.show, R.animator.hide)
            ?.remove(this)
            ?.commit()

        onRemoveListeners.forEach { it.invoke() }
        onRemoveListeners.clear()
    }

    fun log(any: Any) {
        Log.d(javaClass.simpleName, any.toString())
    }

}