package com.viseator.montagecam.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.google.android.cameraview.AspectRatio
import com.google.android.cameraview.CameraView
import com.google.android.cameraview.MocaOnScrollListener
import com.viseator.montagecam.R
import com.viseator.montagecam.base.BaseActivity
import com.viseator.montagecam.fragment.AspectRatioFragment
import com.viseator.montagecam.fragment.CompressResultFragment
import com.viseator.montagecam.fragment.DownloadFragment
import com.viseator.montagecam.util.BitmapUtil
import com.viseator.montagecam.util.ExifUtil
import com.xinlan.imageeditlibrary.editimage.EditImageActivity
import com.xinlan.imageeditlibrary.editimage.utils.BitmapUtils
import com.xinlan.imageeditlibrary.editimage.utils.FileUtil
import kotlinx.android.synthetic.main.activity_camera.camera_album_button
import kotlinx.android.synthetic.main.activity_camera.camera_main_back
import kotlinx.android.synthetic.main.activity_camera.camera_shot_button
import kotlinx.android.synthetic.main.activity_camera.camera_toolbar
import kotlinx.android.synthetic.main.activity_camera.hollow_image
import kotlinx.android.synthetic.main.activity_camera.main_camera_view
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Camera Activity
 * @author viseator
 */
class CameraActivity : BaseActivity(), AspectRatioFragment.Listener {
    val REQUEST_CAMERA_PERMISSION = 0x1
    val REQUEST_STORAGE_PERMISSION = 0x2
    val CALL_EDIT_ACTIVITY = 0x3
    val CALL_CHOOSE_IMAGE_REQUEST = 0x4
    val TAG = "@vir CameraActivity"
    private val ALPHA_SPEED = 500
    private val FLASH_OPTIONS = intArrayOf(CameraView.FLASH_AUTO, CameraView.FLASH_OFF,
            CameraView.FLASH_ON)
    private val FLASH_ICONS = intArrayOf(R.drawable.icon_flash_auto, R.drawable.icon_flash_off,
            R.drawable.icon_flash)
    private val FLASH_TITLES = intArrayOf(R.string.flash_auto, R.string.flash_off,
            R.string.flash_on)
    private val FRAGMENT_DIALOG = "tokenInputFragment"

    private val realMetrics = DisplayMetrics()
    private var mCurrentFlash: Int = 0
    private var inHollowMode = false
    private var mToken: String? = null
    private var mBackgroundHandler: Handler? = null
    private var downloadFragment: DownloadFragment? = null
    var compressResultFragment: CompressResultFragment? = null
    private val mMocaScrollListener = MocaOnScrollListener { e1, e2, distanceX, distanceY ->
        val ratioY = distanceY / realMetrics.heightPixels
        if (e2.x !in realMetrics.widthPixels * 0.7..realMetrics.widthPixels.toDouble()) {
            return@MocaOnScrollListener false
        }
        hollow_image.paintAlpha = (hollow_image.paintAlpha + ratioY * ALPHA_SPEED).toInt()
        true
    }
    private val mCallback = object : CameraView.Callback() {
        override fun onCameraOpened(cameraView: CameraView) {
        }

        override fun onCameraClosed(cameraView: CameraView) {
        }

        override fun onPictureTaken(cameraView: CameraView, data: ByteArray) {
            camera_shot_button.visibility = View.GONE
            val rotation = ExifUtil.getOrientation(data).toFloat()
            getBackgroundHandler().post {
                val file = File(externalCacheDir, "shoot_temp.jpg")
                Log.d(TAG, String().plus(file.absolutePath))
                var os: OutputStream? = null
                try {
                    os = FileOutputStream(file)
                    os.write(data)
                } catch (e: IOException) {
                    Log.w(TAG, "Cannot write to " + file, e)
                } finally {
                    if (os != null) {
                        try {
                            os.close()
                        } catch (e: IOException) {
                        }

                    }
                }
                FileUtil.ablumUpdate(this@CameraActivity, file.absolutePath)
                if (inHollowMode) {
                    startComposeImage(file, rotation)
                } else {
                    startImageEdit(file.absolutePath, rotation)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        checkPermission()
        setFullScreen()
        setContentView(R.layout.activity_camera)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            main_camera_view.start()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION)
        }
        val ratios = main_camera_view.supportedAspectRatios
        for (ratio in ratios) {
            if (ratio.toString() == "16:9") {
                main_camera_view.setAspectRatio(ratio)
                break
            }
        }
        if (camera_shot_button.visibility != View.VISIBLE) {
            camera_shot_button.visibility = View.VISIBLE
        }
        if (compressResultFragment?.isAdded == true) {
            camera_shot_button.visibility = View.VISIBLE
            val fragmentTrans = supportFragmentManager.beginTransaction()
            fragmentTrans.remove(compressResultFragment)
            fragmentTrans.commit()
            compressResultFragment = null
        }
    }

    override fun onStop() {
        main_camera_view.stop()
        super.onStop()
    }

    override fun init() {
        windowManager.defaultDisplay.getRealMetrics(realMetrics)
        when (intent.getStringExtra(MainActivity.TOKEN)) {
            null -> {
                inHollowMode = false
            }
            else -> {
                inHollowMode = true
                mToken = intent.getStringExtra(MainActivity.TOKEN)
                if (mToken == "error") {
                    finish()
                } else {
                    initHollowView()
                }
            }
        }
        val filePath = intent.getStringExtra(MainActivity.BITMAP_FILE)
        if (filePath != null) {
            inHollowMode = true
            main_camera_view.setMocaOnScrollListener(mMocaScrollListener)
            val options = BitmapFactory.Options()
            options.inScaled = false
            val bitmap = BitmapFactory.decodeFile(filePath, options)
            hollow_image.bitmap = bitmap
            hollow_image.visibility = View.VISIBLE
        }
    }

    override fun initView() {
        setSupportActionBar(camera_toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        main_camera_view.addCallback(mCallback)
        main_camera_view.setManualFocus(true)
        main_camera_view.setPinchZoom(true)
        camera_main_back.setOnClickListener({
            onBackPressed()
        })
        camera_shot_button.setOnClickListener({
            main_camera_view.takePicture()
        })
        if (!inHollowMode) {
            camera_album_button.visibility = View.VISIBLE
            camera_album_button.setOnClickListener({
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                        Intent.createChooser(intent, resources.getString(R.string.choose_image)),
                        CALL_CHOOSE_IMAGE_REQUEST)
            })
        }
    }

    fun initHollowView() {
        main_camera_view.setMocaOnScrollListener(mMocaScrollListener)
        downloadFragment = DownloadFragment()
        downloadFragment?.listener = View.OnClickListener {
            if (downloadFragment?.failed!!) {
                downloadFragment?.restart()
                startDownload()
            }
        }
        showDownloadFragment()
        startDownload()
    }

    fun startDownload() {
        val file = File(externalCacheDir, "download.png")
        AndroidNetworking.cancelAll()
        AndroidNetworking.download(resources.getString(R.string.server_download) + mToken + ".png",
                file.parent, "download.png").build().setDownloadProgressListener(
                { bytesDownloaded, totalBytes ->
                    downloadFragment?.setDownloadProgress(
                            ((bytesDownloaded / totalBytes.toFloat() * 100).toInt()))
                }).startDownload(object : DownloadListener {
            override fun onError(anError: ANError?) {
                Log.d(TAG, anError?.message)
                downloadFragment?.fail()

            }

            override fun onDownloadComplete() {
                Log.d(TAG, "download done")
                if (downloadFragment?.isAdded == false) {
                    return
                }
                hideDownloadFragment()
                val options = BitmapFactory.Options()
                options.inScaled = false
                hollow_image.bitmap = BitmapFactory.decodeFile(file.absolutePath)
                hollow_image.visibility = View.VISIBLE
            }

        })
    }

    fun startComposeImage(file: File, rotation: Float) {
        val options = BitmapFactory.Options()
        Log.d(TAG, "realdisplay: ${realMetrics.widthPixels} x ${realMetrics.heightPixels}")
        options.inScaled = false
        val bgImg = BitmapFactory.decodeFile(file.absolutePath, options)
        val rotatedImg = BitmapUtils.rotateBitmap(bgImg, rotation)
        val resultBitmap = BitmapUtil.composeBitmap(rotatedImg!!, hollow_image.bitmap!!,
                realMetrics.widthPixels, realMetrics.heightPixels, hollow_image.scale!!,
                hollow_image.dX!!, hollow_image.dY!!,
                main_camera_view.facing == CameraView.FACING_FRONT)
        val task = UploadImageTask()
        task.execute(resultBitmap)
    }

    /**
     * Background task to upload image
     * @author viseator
     */
    inner class UploadImageTask : AsyncTask<Bitmap, Unit, Unit>() {
        var fileOutput: File? = null
        lateinit var bitmap: Bitmap

        override fun onPreExecute() {
            super.onPreExecute()
            compressResultFragment = CompressResultFragment()
            val df = SimpleDateFormat("yyyyMMddHHmmss")
            val time = df.format(Date())
            fileOutput = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "MontageCam$time.png")
            val fragmentTrans = supportFragmentManager.beginTransaction()
            fragmentTrans.add(R.id.camera_main_constraintlayout, compressResultFragment)
            fragmentTrans.commit()
        }

        override fun doInBackground(vararg params: Bitmap?) {
            BitmapUtils.saveBitmap(params[0], fileOutput?.absolutePath)
            bitmap = params[0]!!
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            Log.d(TAG, "Save Done")
            if (compressResultFragment?.isAdded == false) {
                return
            }
            FileUtil.ablumUpdate(this@CameraActivity, fileOutput?.absolutePath)
            compressResultFragment!!.showImage(bitmap,
                    this@CameraActivity.resources.getString(R.string.file_has_save_to),
                    fileOutput?.absolutePath!!)
        }
    }

    fun startImageEdit(path: String, rotation: Float) {
        val fileOutput = File(externalCacheDir, "picture_output.png")
        EditImageActivity.start(this, path, fileOutput.absolutePath,
                main_camera_view.facing == CameraView.FACING_FRONT, rotation, CALL_EDIT_ACTIVITY)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.aspect_ratio -> {
                val fragmentManager = supportFragmentManager
                if (fragmentManager.findFragmentByTag(FRAGMENT_DIALOG) == null) {
                    val ratios = main_camera_view.supportedAspectRatios
                    val currentRatio = main_camera_view.aspectRatio
                    AspectRatioFragment.newInstance(ratios, currentRatio).show(fragmentManager,
                            FRAGMENT_DIALOG)
                }
                return true
            }
            R.id.switch_flash -> {
                mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.size
                item.setTitle(FLASH_TITLES[mCurrentFlash])
                item.setIcon(FLASH_ICONS[mCurrentFlash])
                main_camera_view.flash = FLASH_OPTIONS[mCurrentFlash]
                return true
            }
            R.id.switch_camera -> {
                val facing = main_camera_view.facing
                main_camera_view.facing =
                        if (facing == CameraView.FACING_FRONT) CameraView.FACING_BACK
                        else CameraView.FACING_FRONT
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAspectRatioSelected(ratio: AspectRatio) {
        Toast.makeText(this, ratio.toString(), Toast.LENGTH_SHORT).show()
        main_camera_view.setAspectRatio(ratio)
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
        if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION)
        }
    }

    override fun onBackPressed() {
        if (compressResultFragment?.isAdded == true) {
            camera_shot_button.visibility = View.VISIBLE
            val fragmentTrans = supportFragmentManager.beginTransaction()
            fragmentTrans.remove(compressResultFragment)
            fragmentTrans.commit()
            compressResultFragment = null
        } else if (downloadFragment?.isAdded == true) {
            hideDownloadFragment()
            finish()
        } else {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CALL_CHOOSE_IMAGE_REQUEST && data != null) {
                LoadImageTask().execute(data.data)
            }
        }
    }

    /**
     * Background task to load image
     */
    inner class LoadImageTask : AsyncTask<Uri, Unit, Unit>() {
        val file = File(externalCacheDir, "shoot_temp.jpg")
        val dialog = getLoadingDialog(this@CameraActivity, R.string.loading, false)

        override fun onPreExecute() {
            dialog.show()
        }

        override fun doInBackground(vararg params: Uri?) {
            val imageStream = contentResolver.openInputStream(params[0])
            val bitmap = BitmapFactory.decodeStream(imageStream)
            BitmapUtils.saveBitmap(bitmap, file.absolutePath)
        }

        override fun onPostExecute(result: Unit?) {
            dialog.dismiss()
            startImageEdit(file.absolutePath, 0f)
        }
    }

    fun showDownloadFragment() {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.add(R.id.camera_main_constraintlayout, downloadFragment)
        fragmentTrans.commit()
    }

    fun hideDownloadFragment() {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.remove(downloadFragment)
        fragmentTrans.commit()
    }
}
