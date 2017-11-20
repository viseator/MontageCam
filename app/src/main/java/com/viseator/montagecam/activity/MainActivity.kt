package com.viseator.montagecam.activity

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
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
import com.viseator.montagecam.view.TokenInputDialog
import com.viseator.montagecam.view.OnInputDialogResultListener
import com.xinlan.imageeditlibrary.editimage.EditImageActivity
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class MainActivity : BaseActivity() {

    val TAG = "@vir MainActivity"
    val dialog = TokenInputDialog()
    lateinit var clipManager: ClipboardManager

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
        clipManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        AndroidNetworking.initialize(applicationContext)
        AndroidNetworking.setParserFactory(JacksonParserFactory())
        LocalBroadcastManager.getInstance(this).registerReceiver(CameraActivityReceiver(),
                IntentFilter(EditImageActivity.INTENT_START_CAMERA_ACTIVITY))
    }

    override fun initView() {
        dialog.resultListener = inputListener
        takePhotoButton.setOnClickListener({
            startActivity<CameraActivity>()
        })
        receiverPhotoButton.setOnClickListener({
            dialog.editText.text = null
            dialog.show(fragmentManager, null)
        })
    }

    val inputListener = object : OnInputDialogResultListener {
        override fun onResult(result: String) {
            dialog.dismiss()
            startActivity<CameraActivity>(TOKEN to getToken(result))
        }

    }

    fun getToken(s: String): String {
        var sub: String = ""
        try {
            var i = s.indexOf("|")
            sub = s.substring(i + 1)
            i = sub.indexOf("|")
            sub = sub.substring(0, i)
        } catch (e: StringIndexOutOfBoundsException) {
            return "error"
        }
        if (sub.length != 8) {
            return "error"
        }
        return sub
    }

    override fun onResume() {
        super.onResume()
        val clipData = if (clipManager.hasPrimaryClip() && clipManager.primaryClipDescription.hasMimeType(
                ClipDescription.MIMETYPE_TEXT_PLAIN)) {
            clipManager.primaryClip
        } else {
            return
        }
        val token = getToken(clipData.getItemAt(0).text.toString())
        if (token != "error") {
            toast(resources.getString(R.string.recognizedToken))
            dialog.token = "|$token|"
            dialog.show(fragmentManager, null)
        }
    }

    override fun onPause() {
        super.onPause()
        if (dialog.isAdded) {
            dialog.dismiss()
        }
    }
}

