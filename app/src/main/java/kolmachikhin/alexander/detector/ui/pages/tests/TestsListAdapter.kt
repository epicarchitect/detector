package kolmachikhin.alexander.detector.ui.pages.tests

import android.view.View
import kolmachikhin.alexander.detector.R
import kolmachikhin.alexander.detector.controllers.tests.TestModel
import kolmachikhin.alexander.detector.ui.base.adapters.lists.ModelListAdapter
import kotlinx.android.synthetic.main.test_item.view.*

class TestsListAdapter : ModelListAdapter<TestModel, TestsListAdapter.Holder>() {

    override fun areContentsTheSame(oldItem: TestModel, newItem: TestModel) =
        oldItem.name == newItem.name

    override val itemLayout = R.layout.test_item

    override fun createViewHolder(view: View) = Holder(view)

    inner class Holder(view: View) : ModelListAdapter<TestModel, TestsListAdapter.Holder>.ViewHolder<TestModel>(view) {

        override fun bind(model: TestModel) = with (itemView) {
            tvName.text = model.name
        }

    }
}