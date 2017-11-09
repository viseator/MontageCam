package com.viseator.montagecam

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import butterknife.BindView
import com.google.android.cameraview.AspectRatio
import com.google.android.cameraview.CameraView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class MainActivity : BaseActivity(), AspectRatioFragment.Listener {
    val REQUEST_CAMERA_PERMISSION = 0x1
    val REQUEST_STORAGE_PERMISSION = 0x2
    val TAG = "@vir MainActivity"

    @BindView(R.id.main_camera_view)
    lateinit var mCameraView: CameraView
    @BindView(R.id.camera_toolbar)
    lateinit var mToolBar: Toolbar
    @BindView(R.id.camera_shot_button)
    lateinit var mShotButton: ImageButton
    private val FLASH_OPTIONS = intArrayOf(CameraView.FLASH_AUTO, CameraView.FLASH_OFF,
            CameraView.FLASH_ON)

    private val FLASH_ICONS = intArrayOf(R.drawable.ic_flash_auto, R.drawable.ic_flash_off,
            R.drawable.ic_flash_on)

    private val FLASH_TITLES = intArrayOf(R.string.flash_auto, R.string.flash_off,
            R.string.flash_on)
    private val FRAGMENT_DIALOG = "dialog"
    private var mCurrentFlash: Int = 0

    private var mBackgroundHandler: Handler? = null
    private val mCallback = object : CameraView.Callback() {

        override fun onCameraOpened(cameraView: CameraView) {
            Log.d(TAG, "onCameraOpened")
        }

        override fun onCameraClosed(cameraView: CameraView) {
            Log.d(TAG, "onCameraClosed")
        }

        override fun onPictureTaken(cameraView: CameraView, data: ByteArray) {
            Log.d(TAG, "onPictureTaken " + data.size)
            Toast.makeText(cameraView.context, R.string.picture_taken, Toast.LENGTH_SHORT)
                    .show()
            getBackgroundHandler().post {
                val file = File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES),
                        "picture.jpg")
                Log.d(TAG, String().plus(file.absolutePath))
                var os: OutputStream? = null
                try {
                    os = FileOutputStream(file)
                    os.write(data)
                    os.close()
                } catch (e: IOException) {
                    Log.w(TAG, "Cannot write to " + file, e)
                } finally {
                    if (os != null) {
                        try {
                            os.close()
                        } catch (e: IOException) {
                            // Ignore
                        }

                    }
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        checkPermission()
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
        val ratios = mCameraView.supportedAspectRatios
        for (ratio in ratios) {
            if (ratio.toString() == "16:9") {
                mCameraView.setAspectRatio(ratio)
                break
            }
        }
    }

    override fun onStop() {
        mCameraView.stop()
        super.onStop()
    }

    override fun initView() {
        setSupportActionBar(mToolBar)
        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        mCameraView.addCallback(mCallback)
        mShotButton.setOnClickListener({
            mCameraView.takePicture()
        })
    }

    override fun init() {
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.aspect_ratio -> {
                val fragmentManager = supportFragmentManager
                if (fragmentManager.findFragmentByTag(
                        FRAGMENT_DIALOG) == null) {
                    val ratios = mCameraView.supportedAspectRatios
                    val currentRatio = mCameraView.aspectRatio
                    AspectRatioFragment.newInstance(ratios, currentRatio)
                            .show(fragmentManager, FRAGMENT_DIALOG)
                }
                return true
            }
            R.id.switch_flash -> {
                mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.size
                item.setTitle(FLASH_TITLES[mCurrentFlash])
                item.setIcon(FLASH_ICONS[mCurrentFlash])
                mCameraView.flash = FLASH_OPTIONS[mCurrentFlash]
                return true
            }
            R.id.switch_camera -> {
                val facing = mCameraView.facing
                mCameraView.facing = if (facing == CameraView.FACING_FRONT)
                    CameraView.FACING_BACK
                else
                    CameraView.FACING_FRONT
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAspectRatioSelected(ratio: AspectRatio) {
        Toast.makeText(this, ratio.toString(), Toast.LENGTH_SHORT).show()
        mCameraView.setAspectRatio(ratio)
    }

    private fun getBackgroundHandler(): Handler {
        if (mBackgroundHandler == null) {
            val thread = HandlerThread("background")
            thread.start()
            mBackgroundHandler = Handler(thread.looper)
        }
        return mBackgroundHandler as Handler
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission
                    .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_PERMISSION)
        }
    }

}
