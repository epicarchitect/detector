package kolmachikhin.alexander.detector.ui.pages.scanned_tests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kolmachikhin.alexander.bottomappbar.ui.base.adapters.spinner.SpinnerItem
import kolmachikhin.alexander.detector.R
import kolmachikhin.alexander.detector.controllers.scanned_tests.ScannedTestModel
import kolmachikhin.alexander.detector.controllers.scanned_tests.ScannedTestsController
import kolmachikhin.alexander.detector.controllers.tests.TestModel
import kolmachikhin.alexander.detector.controllers.tests.TestsController
import kolmachikhin.alexander.detector.ui.MainActivity
import kolmachikhin.alexander.detector.ui.anylise.AnalyseFragment
import kolmachikhin.alexander.detector.ui.base.adapters.spinner.SpinnerAdapter
import kolmachikhin.alexander.detector.ui.extentions.app
import kolmachikhin.alexander.detector.ui.extentions.text
import kotlinx.android.synthetic.main.start_scan_test_dialog.*

class StartScanTestDialog : DialogFragment() {

    val tests = ArrayList<TestModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.start_scan_test_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSpinnerTests()



        buttonScan.setOnClickListener {
            val controller = ScannedTestsController(app())
            val name = etName.text()
            val test = tests[testSpinner.selectedItemPosition]

            dismiss()

            MainActivity.instance?.let { activity ->
                val analyseFragment = AnalyseFragment()

                analyseFragment.onDone { answers ->
                    controller.save(
                        ScannedTestModel(
                            studentName = name,
                            test = test,
                            answers = answers
                        )
                    )
                    analyseFragment.remove()
                }

                activity.addFragment(analyseFragment)
            }
        }
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

}