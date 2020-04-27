package kolmachikhin.alexander.detector.ui.pages.print

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import kolmachikhin.alexander.detector.R
import kolmachikhin.alexander.detector.ui.base.BaseFragment
import kotlinx.android.synthetic.main.print_fragment.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class PrintFragment(override val layout: Int = R.layout.print_fragment) : BaseFragment() {

    override fun start() {
        buttonDownload.setOnClickListener {
            activity.ifAllPermissionsGranted {
                download()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun download() {
        Thread {
            val dir = Environment.getExternalStoragePublicDirectory("Detector")

            if (!dir.exists())
                dir.mkdirs()

            val file = File(dir, "blank_a4.png")

            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.two_blanks)

            val outStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            outStream.flush()
            outStream.close()

            runOnUi {
                tvStatus.text = "Сохранено в:\n" + file.path
            }
        }.start()
    }

}