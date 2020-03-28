package kolmachikhin.alexander.detector.ui

import android.animation.Animator
import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import kolmachikhin.alexander.detector.R
import kolmachikhin.alexander.detector.ui.anylise.AnalyseFragment
import kolmachikhin.alexander.detector.ui.camera.CameraFragment
import kotlinx.android.synthetic.main.main_fragment.*
import mini.SuperFragment
import kotlin.math.roundToInt

class MainFragment(override val layout: Int = R.layout.main_fragment) : SuperFragment() {

    override fun start() {
        addChild(R.id.container, AnalyseFragment())
    }

}