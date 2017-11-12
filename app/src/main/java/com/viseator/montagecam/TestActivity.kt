package com.viseator.montagecam

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.startActivity

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        /*        val file = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "picture.jpg")
                val outputFile = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "picture_output.png")*/
        //        EditImageActivity.start(this, file.absolutePath, outputFile.absolutePath, 1)
        startActivity<CameraActivity>()
        finish()
    }
}

