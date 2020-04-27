package kolmachikhin.alexander.detector.ui.pages.tests

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kolmachikhin.alexander.detector.R
import kolmachikhin.alexander.detector.controllers.tests.TestModel
import kolmachikhin.alexander.detector.ui.answers.AnswersListAdapter
import kolmachikhin.alexander.detector.ui.base.dialogs.ModelDialog
import kolmachikhin.alexander.detector.ui.extentions.disableViews
import kolmachikhin.alexander.detector.ui.extentions.enableViews
import kolmachikhin.alexander.detector.ui.extentions.text
import kotlinx.android.synthetic.main.test_dialog.*
import java.util.concurrent.Executors

class TestDialog(model: TestModel = TestModel()) : ModelDialog<TestModel>(model) {

    override val innerLayout = R.layout.test_dialog

    val answersAdapter = AnswersListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvAnswers.layoutManager = LinearLayoutManager(context)
        rvAnswers.setHasFixedSize(true)
        rvAnswers.adapter = answersAdapter
    }

    override fun setViewsByModel() {
        etName.setText(model.name)
        answersAdapter.list = ArrayList(model.answers)
    }

    override fun createModelByViews(oldModel: TestModel) =
        oldModel.copy(
            name = etName.text(),
            answers = answersAdapter.list
        )

    override fun onModeView() {
        disableViews(etName)
        answersAdapter.isMutable = false
    }

    override fun onModeEdit() {
        enableViews(etName)
        answersAdapter.isMutable = true
    }


}