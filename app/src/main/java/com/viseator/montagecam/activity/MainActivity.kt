package com.viseator.montagecam.activity

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.widget.ImageView
import butterknife.BindView
import com.androidnetworking.AndroidNetworking
import com.jacksonandroidnetworking.JacksonParserFactory
import com.viseator.montagecam.R
import com.viseator.montagecam.base.BaseActivity
import com.viseator.montagecam.fragment.InfoFragment
import com.viseator.montagecam.fragment.OnInputDialogResultListener
import com.viseator.montagecam.fragment.TokenInputFragment
import com.viseator.montagecam.receiver.CameraActivityReceiver
import com.xinlan.imageeditlibrary.editimage.EditImageActivity
import org.jetbrains.anko.startActivity


class MainActivity : BaseActivity() {

    val TAG = "@vir MainActivity"
    val tokenInputFragment = TokenInputFragment()
    val infoFragment = InfoFragment()
    lateinit var clipManager: ClipboardManager


    companion object {
        val TOKEN = "token"
        val BITMAP_FILE = "bitmap"
    }

    @BindView(R.id.main_take_photo_button) lateinit var takePhotoButton: ImageView
    @BindView(R.id.main_receive_photo_button) lateinit var receiverPhotoButton: ImageView
    @BindView(R.id.main_info_button) lateinit var infoButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        setFullScreen()
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
    }

    override fun init() {
        clipManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        AndroidNetworking.initialize(applicationContext)
        AndroidNetworking.setParserFactory(JacksonParserFactory())
        LocalBroadcastManager.getInstance(this).registerReceiver(CameraActivityReceiver(),
                IntentFilter(EditImageActivity.INTENT_START_CAMERA_ACTIVITY))
        tokenInputFragment.listener = View.OnClickListener { hideInputFragment() }
        infoFragment.listener = View.OnClickListener { hideInfoFragment() }
    }

    override fun initView() {
        tokenInputFragment.resultListener = inputListener
        infoButton.setOnClickListener({
            showInfoFragment()
        })
        takePhotoButton.setOnClickListener({
            startActivity<CameraActivity>()
        })
        receiverPhotoButton.setOnClickListener({
            showInputFragment()
        })

    }

    val inputListener = object : OnInputDialogResultListener {
        override fun onResult(result: String) {
            hideInputFragment()
            val token = getToken(result)
            if (token == "error") {
                showSnackBar(R.id.main_constraintlayout, R.string.error_token)
            } else {
                startActivity<CameraActivity>(TOKEN to getToken(result))
            }
        }

    }

    fun showInputFragment() {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.add(R.id.main_constraintlayout, tokenInputFragment)
        fragmentTrans.commit()
    }

    fun hideInputFragment() {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.remove(tokenInputFragment)
        fragmentTrans.commit()
    }

    fun showInfoFragment() {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.add(R.id.main_constraintlayout, infoFragment)
        fragmentTrans.commit()
    }

    fun hideInfoFragment() {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.remove(infoFragment)
        fragmentTrans.commit()
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
            showSnackBar(R.id.main_constraintlayout, R.string.recognizedToken)
            tokenInputFragment.token = "|$token|"
            showInputFragment()
        }
    }

    override fun onPause() {
        super.onPause()
        if (tokenInputFragment.isAdded) {
            hideInputFragment()
        }
    }

    override fun onBackPressed() {
        if (tokenInputFragment.isAdded == true) {
            hideInputFragment()
        } else if (infoFragment.isAdded == true) {
            hideInfoFragment()
        } else {
            finish()
        }
    }

}

