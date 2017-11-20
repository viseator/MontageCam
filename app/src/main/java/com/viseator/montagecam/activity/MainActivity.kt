package com.viseator.montagecam.activity

import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Button
import butterknife.BindView
import com.androidnetworking.AndroidNetworking
import com.jacksonandroidnetworking.JacksonParserFactory
import com.viseator.montagecam.R
import com.viseator.montagecam.base.BaseActivity
import com.viseator.montagecam.receiver.CameraActivityReceiver
import com.viseator.montagecam.view.InputDialog
import com.viseator.montagecam.view.OnInputDialogResultListener
import com.xinlan.imageeditlibrary.editimage.EditImageActivity
import org.jetbrains.anko.startActivity


class MainActivity : BaseActivity() {

    val TAG = "@vir MainActivity"
    val label = "LABEL"
    val dialog = InputDialog()

    companion object {
        val TOKEN = "token"
        val BITMAP_FILE = "bitmap"
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
        LocalBroadcastManager.getInstance(this).registerReceiver(CameraActivityReceiver(),
                IntentFilter(EditImageActivity.INTENT_START_CAMERA_ACTIVITY))
    }

    override fun initView() {
        takePhotoButton.setOnClickListener({
            startActivity<CameraActivity>()
        })
        receiverPhotoButton.setOnClickListener({
            dialog.resultListener = inputListener
            dialog.show(fragmentManager, label)
        })
    }

    val inputListener = object : OnInputDialogResultListener {
        override fun onResult(result: String) {
            Log.d(TAG, "token: $result")
            dialog.dismiss()
            startActivity<CameraActivity>(TOKEN to result)
        }

    }

}

