package com.viseator.montagecam.activity

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import butterknife.BindView
import com.androidnetworking.AndroidNetworking
import com.jacksonandroidnetworking.JacksonParserFactory
import com.viseator.montagecam.R
import com.viseator.montagecam.base.BaseActivity
import com.viseator.montagecam.view.InputDialog
import com.viseator.montagecam.view.OnInputDialogResultListener
import com.xinlan.imageeditlibrary.editimage.EditImageActivity
import org.jetbrains.anko.startActivity
import java.io.File


class MainActivity : BaseActivity() {

    val TAG = "@vir MainActivity"
    val label = "LABEL"
    companion object {
        val TOKEN = "token"
    }
    @BindView(R.id.main_take_photo_button) lateinit var takePhotoButton: Button
    @BindView(R.id.main_receive_photo_button) lateinit var receiverPhotoButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
//        startActivity<CameraActivity>(TOKEN to "9dfb96a5")
    }

    override fun init() {
        AndroidNetworking.initialize(applicationContext)
        AndroidNetworking.setParserFactory(JacksonParserFactory())
    }

    override fun initView() {
        takePhotoButton.setOnClickListener({
            startActivity<CameraActivity>()
        })
        receiverPhotoButton.setOnClickListener({
            val dialog = InputDialog()
            dialog.resultListener = inputListener
            dialog.show(fragmentManager, label)
        })
    }

    val inputListener = object : OnInputDialogResultListener {
        override fun onResult(result: String) {
            Log.d(TAG, "token: $result")
            startActivity<CameraActivity>(TOKEN to result)
        }

    }

}

