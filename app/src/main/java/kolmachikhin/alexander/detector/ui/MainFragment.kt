package kolmachikhin.alexander.detector.ui

import kolmachikhin.alexander.detector.R
import kolmachikhin.alexander.detector.ui.pages.print.PrintFragment
import kolmachikhin.alexander.detector.ui.pages.scanned_tests.ScannedTestsFragment
import kolmachikhin.alexander.detector.ui.pages.tests.TestsFragment
import kotlinx.android.synthetic.main.main_fragment.*
import kolmachikhin.alexander.detector.ui.base.BaseFragment

class MainFragment(override val layout: Int = R.layout.main_fragment) : BaseFragment() {

    override fun start() {
        setNavigation()
        navigation.selectedItemId = R.id.scannedTestsFragment
    }

    fun setNavigation() {
        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.testsFragment -> TestsFragment()
                R.id.scannedTestsFragment -> ScannedTestsFragment()
                else -> PrintFragment()
            }.let { replaceChild(R.id.fragmentContainer, it) }
            true
        }
    }
}