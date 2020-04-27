package kolmachikhin.alexander.detector.ui.pages.scanned_tests

import android.view.View
import kolmachikhin.alexander.detector.R
import kolmachikhin.alexander.detector.controllers.scanned_tests.ScannedTestModel
import kolmachikhin.alexander.detector.ui.base.adapters.lists.ModelListAdapter
import kotlinx.android.synthetic.main.scanned_test_item.view.*

class ScannedTestsListAdapter : ModelListAdapter<ScannedTestModel, ScannedTestsListAdapter.Holder>() {

    override val itemLayout = R.layout.scanned_test_item

    override fun areContentsTheSame(oldItem: ScannedTestModel, newItem: ScannedTestModel) =
        oldItem.studentName == newItem.studentName &&
                oldItem.test == newItem.test

    override fun createViewHolder(view: View) = Holder(view)

    inner class Holder(view: View) : ModelListAdapter<ScannedTestModel, Holder>.ViewHolder<ScannedTestModel>(view) {
        override fun bind(model: ScannedTestModel) = with (itemView) {
            tvName.text = model.test.name
            tvStudent.text = model.studentName
        }
    }
}