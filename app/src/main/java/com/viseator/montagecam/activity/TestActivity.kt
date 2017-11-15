package com.viseator.montagecam.activity

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.viseator.montagecam.R
import com.xinlan.imageeditlibrary.editimage.EditImageActivity
import org.jetbrains.anko.startActivity
import java.io.File
import com.jacksonandroidnetworking.JacksonParserFactory



class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidNetworking.initialize(applicationContext)
        AndroidNetworking.setParserFactory(JacksonParserFactory())
        setContentView(R.layout.activity_test)
        val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "picture.png")
        val outputFile = File(
                Environment.getDataDirectory(),
                "temp.png")
        EditImageActivity.start(this, file.absolutePath, outputFile.absolutePath, 1)
//                startActivity<CameraActivity>()
        finish()
    }
}

