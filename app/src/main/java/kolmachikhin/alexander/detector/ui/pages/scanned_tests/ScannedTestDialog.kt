package kolmachikhin.alexander.detector.ui.pages.scanned_tests

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kolmachikhin.alexander.bottomappbar.ui.base.adapters.spinner.SpinnerItem
import kolmachikhin.alexander.detector.R
import kolmachikhin.alexander.detector.constants.TEST_COLUMNS_IN_BLOCK
import kolmachikhin.alexander.detector.constants.TEST_ROWS_IN_BOCK
import kolmachikhin.alexander.detector.controllers.scanned_tests.ScannedTestModel
import kolmachikhin.alexander.detector.controllers.tests.TestModel
import kolmachikhin.alexander.detector.controllers.tests.TestsController
import kolmachikhin.alexander.detector.ui.base.adapters.spinner.SpinnerAdapter
import kolmachikhin.alexander.detector.ui.base.dialogs.ModelDialog
import kolmachikhin.alexander.detector.ui.extentions.*
import kotlinx.android.synthetic.main.scanned_test_dialog.*
import kotlinx.android.synthetic.main.scanned_test_dialog.etName

class ScannedTestDialog(model: ScannedTestModel = ScannedTestModel()) : ModelDialog<ScannedTestModel>(model) {

    override val innerLayout = R.layout.scanned_test_dialog

    val tests = ArrayList<TestModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSpinnerTests()
    }

    fun setSpinnerTests() {
        tests.clear()
        TestsController(app()).getTests().forEach {
            tests.add(it)
        }

        val items = tests.map {
            SpinnerItem(it.id, it.name)
        }

        testSpinner.adapter = SpinnerAdapter(requireContext(), items)
    }

    override fun setViewsByModel() {
        etName.setText(model.studentName)
        setTestSpinnerPosition()
        setAnswers()
    }

    fun setAnswers() {
        answersContainer.removeAllViews()
        for (row in 0 until TEST_ROWS_IN_BOCK) {
            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.HORIZONTAL
            layout.gravity = Gravity.CENTER
            answersContainer.addView(layout)

            for (column in 0 until TEST_COLUMNS_IN_BLOCK) {
                val index = TEST_COLUMNS_IN_BLOCK * row + column
                val studentAnswer = model.answers[index]
                val goodAnswer = model.test.answers[index]

                val tv = TextView(context)
                tv.text = studentAnswer.name
                tv.gravity = Gravity.CENTER
                tv.setPadding(dp(2), dp(2), dp(2), dp(2))
                tv.setTextColor(
                    when (studentAnswer == goodAnswer) {
                        true -> Color.GREEN
                        else -> Color.RED
                    }
                )
                layout.addView(tv)

                val params = tv.layoutParams as LinearLayout.LayoutParams
                params.width = dp(15)
                params.height = dp(20)
                tv.layoutParams = params
            }
        }
    }

    fun setTestSpinnerPosition() {
        if (tests.size <= 0) return

        var position = 0
        val selectedId = (testSpinner.selectedItem as SpinnerItem).id
        tests.forEach {
            if (it.id == selectedId) {
                testSpinner.setSelection(position)
                return
            }
            position++
        }
    }

    override fun createModelByViews(oldModel: ScannedTestModel) =
        oldModel.copy(
            studentName = etName.text(),
            test = tests[testSpinner.selectedItemPosition]
        )

    override fun onModeView() {
        disableViews(etName, testSpinner)
    }

    override fun onModeEdit() {
        enableViews(etName, testSpinner)
    }


}