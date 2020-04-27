package kolmachikhin.alexander.detector

import android.app.Application
import android.util.Log
import kolmachikhin.alexander.detector.controllers.scanned_tests.ScannedTestsController
import kolmachikhin.alexander.detector.controllers.tests.TestsController

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        TestsController(this)
        ScannedTestsController(this)
        Log.d("test", "App::onCreate")
    }

}