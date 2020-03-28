package mini

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

abstract class SuperFragment : Fragment() {

    private var isReady = false
    private val onReadyListeners: ArrayList<() -> Unit> = ArrayList()
    private val onRemoveListeners: ArrayList<() -> Unit> = ArrayList()

    val activity get() = super.getActivity() as SuperActivity

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(containerId, f)
                .commit()
        }
    }

    fun replaceChild(containerId: Int, f: Fragment) {
        onReady {
            childFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(containerId, f)
                .commit()
        }
    }

    fun remove() {
        fragmentManager?.beginTransaction()
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            ?.remove(this)
            ?.commit()

        onRemoveListeners.forEach { it.invoke() }
        onRemoveListeners.clear()
    }

    fun log(any: Any) {
        Log.d(javaClass.simpleName, any.toString())
    }

}