package com.viseator.montagecam

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import butterknife.BindView
import com.google.android.cameraview.CameraView

class MainActivity : BaseActivity() {

    private val REQUEST_CAMERA_PERMISSION: Int = 0x1;

    @BindView(R.id.main_camera_view)
    lateinit var mCameraView: CameraView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCameraView.start()
    }

    override fun onResume() {
        super.onResume()
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED
            -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission
                    .CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    override fun initView() {

    }

    override fun init() {
    }

    override fun getLayout(): Int {
        return R.layout.activity_main
    }
}
