package com.viseator.montagecam

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import com.xinlan.imageeditlibrary.editimage.EditImageActivity
import org.jetbrains.anko.startActivity
import java.io.File

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
//                val file = File(
//                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                        "t1.png")
//                val outputFile = File(
//                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                        "picture_test1.png")
//                EditImageActivity.start(this, file.absolutePath, outputFile.absolutePath, 1)
        startActivity<CameraActivity>()
        finish()
    }
}

