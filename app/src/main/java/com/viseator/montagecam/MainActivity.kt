package com.viseator.montagecam

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.Window
import android.view.WindowManager
import butterknife.BindView
import com.google.android.cameraview.CameraView

class MainActivity : BaseActivity() {
    private val REQUEST_CAMERA_PERMISSION: Int = 0x1;

    @BindView(R.id.main_camera_view)
    lateinit var mCameraView: CameraView

    override fun onCreate(savedInstanceState: Bundle?) {
        setFullScreen()
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mCameraView.start()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION)
        }
    }

    override fun initView() {
    }

    override fun init() {
    }

}
