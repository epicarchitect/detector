package kolmachikhin.alexander.detector.ui.pages.tests

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kolmachikhin.alexander.bottomappbar.ui.base.dialogs.Action.*
import kolmachikhin.alexander.detector.R
import kolmachikhin.alexander.detector.controllers.tests.TestsController
import kolmachikhin.alexander.detector.ui.base.BaseFragment
import kolmachikhin.alexander.detector.ui.extentions.app
import kotlinx.android.synthetic.main.tests_fragment.*

class TestsFragment(override val layout: Int = R.layout.tests_fragment) : BaseFragment() {

    val controller get() = TestsController(app())

    val listAdapter = TestsListAdapter()

    override fun start() {
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = listAdapter
        controller.getLiveTests().observe(viewLifecycleOwner, Observer {
            listAdapter.list = it
        })

        listAdapter.onClick { model, _ ->
            val dialog = TestDialog(model)
            dialog.setModeView()

            dialog.onAction { action, model ->
                when (action) {
                    SAVE -> controller.save(model)
                    DELETE -> controller.delete(model)
                }
            }

            dialog.show(parentFragmentManager, "")
        }

        fab.setOnClickListener {
            val dialog = TestDialog()
            dialog.setModeEdit()
            dialog.setForCreate()

            dialog.onAction { action, model ->
                when (action) {
                    SAVE -> controller.save(model)
                }
            }

            dialog.show(parentFragmentManager, "")
        }
    }

}