package kolmachikhin.alexander.detector.ui.pages.scanned_tests

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kolmachikhin.alexander.bottomappbar.ui.base.dialogs.Action
import kolmachikhin.alexander.detector.R
import kolmachikhin.alexander.detector.controllers.scanned_tests.ScannedTestsController
import kolmachikhin.alexander.detector.ui.base.BaseFragment
import kolmachikhin.alexander.detector.ui.extentions.app
import kotlinx.android.synthetic.main.scanned_tests_fragment.*

class ScannedTestsFragment(override val layout: Int = R.layout.scanned_tests_fragment) : BaseFragment() {

    val controller get() = ScannedTestsController(app())

    val listAdapter = ScannedTestsListAdapter()

    override fun start() {
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = listAdapter
        controller.getLiveScannedTests().observe(viewLifecycleOwner, Observer {
            listAdapter.list = it
        })

        listAdapter.onClick { model, _ ->
            val dialog = ScannedTestDialog(model)
            dialog.setModeView()

            dialog.onAction { action, model ->
                when (action) {
                    Action.SAVE -> controller.save(model)
                    Action.DELETE -> controller.delete(model)
                }
            }

            dialog.show(parentFragmentManager, "")
        }

        fab.setOnClickListener {
            val dialog = StartScanTestDialog()
            dialog.show(parentFragmentManager, "")
        }
    }

}