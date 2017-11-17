package com.viseator.montagecam.activity

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
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
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.google.android.cameraview.AspectRatio
import com.google.android.cameraview.CameraView
import com.viseator.montagecam.R
import com.viseator.montagecam.base.BaseActivity
import com.viseator.montagecam.fragment.AspectRatioFragment
import com.viseator.montagecam.util.BitmapUtil
import com.viseator.montagecam.view.HollowImageView
import com.viseator.montagecam.view.ImagePreviewDialog
import com.xinlan.imageeditlibrary.editimage.EditImageActivity
import com.xinlan.imageeditlibrary.editimage.utils.BitmapUtils
import com.xinlan.imageeditlibrary.editimage.utils.FileUtil
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : BaseActivity(), AspectRatioFragment.Listener {
    val REQUEST_CAMERA_PERMISSION = 0x1
    val REQUEST_STORAGE_PERMISSION = 0x2
    val CALL_EDIT_ACTIVITY = 0x3
    val TAG = "@vir CameraActivity"

    @BindView(R.id.main_camera_view) lateinit var mCameraView: CameraView
    @BindView(R.id.camera_toolbar) lateinit var mToolBar: Toolbar
    @BindView(R.id.camera_shot_button) lateinit var mShotButton: ImageButton
    @BindView(R.id.hollow_image) lateinit var mHollowImageView: HollowImageView

    private val FLASH_OPTIONS = intArrayOf(CameraView.FLASH_AUTO, CameraView.FLASH_OFF,
            CameraView.FLASH_ON)

    private val FLASH_ICONS = intArrayOf(R.drawable.ic_flash_auto, R.drawable.ic_flash_off,
            R.drawable.ic_flash_on)

    private val FLASH_TITLES = intArrayOf(R.string.flash_auto, R.string.flash_off,
            R.string.flash_on)
    private val FRAGMENT_DIALOG = "dialog"
    private var mCurrentFlash: Int = 0
    private var inHollowMode = false
    private var mToken: String? = null

    private var mBackgroundHandler: Handler? = null
    private val mCallback = object : CameraView.Callback() {

        override fun onCameraOpened(cameraView: CameraView) {
        }

        override fun onCameraClosed(cameraView: CameraView) {
        }

        override fun onPictureTaken(cameraView: CameraView, data: ByteArray) {
            Log.d(TAG, "onPictureTaken " + data.size)
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
                            // Ignore
                        }

                    }
                }
                FileUtil.ablumUpdate(this@CameraActivity, file.absolutePath)
                toast("Picture Shoot")
                if (inHollowMode) {
                    startComposeImage(file)
                } else {
                    startImageEdit(file)
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

    override fun init() {
        when (intent.getStringExtra(MainActivity.TOKEN)) {
            null -> {
                inHollowMode = false
            }
            else -> {
                inHollowMode = true
                mToken = intent.getStringExtra(MainActivity.TOKEN)
            }
        }
    }

    override fun initView() {
        setSupportActionBar(mToolBar)
        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        mCameraView.addCallback(mCallback)
        mShotButton.setOnClickListener({
            mCameraView.takePicture()
        })
        if (inHollowMode) initHollowView()
    }

    fun initHollowView() {
        val file = File(externalCacheDir, "download.png")
        val dialog = ProgressDialog(this)
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        dialog.setTitle(R.string.downloading)
        dialog.max = 100
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnCancelListener { finish() }
        dialog.show()
        AndroidNetworking.download(resources.getString(R.string.server_download) + mToken + ".png",
                file.parent, "download.png").build().setDownloadProgressListener(
                { bytesDownloaded, totalBytes ->
                    dialog.progress = (bytesDownloaded / totalBytes.toFloat() * 100).toInt()
                }).startDownload(object : DownloadListener {
            override fun onError(anError: ANError?) {
                Log.e(TAG, anError?.errorBody)
                Log.e(TAG, anError?.errorDetail)
                dialog.dismiss()
                alert(resources.getString(R.string.download_error)) {}
            }

            override fun onDownloadComplete() {
                Log.d(TAG, "download done")
                dialog.dismiss()
                val options = BitmapFactory.Options()
                options.inScaled = false
                mHollowImageView.bitmap = BitmapFactory.decodeFile(file.absolutePath)
                mHollowImageView.visibility = View.VISIBLE
            }

        })
    }

    fun startComposeImage(file: File) {
        val metrics = resources.displayMetrics
        val options = BitmapFactory.Options()
        options.inScaled = false
        val bgImg = BitmapFactory.decodeFile(file.absolutePath, options)
        val resultBitmap = BitmapUtil.composeBitmap(bgImg, mHollowImageView.bitmap!!,
                metrics.widthPixels, metrics.heightPixels, mHollowImageView.scale!!,
                mHollowImageView.dX!!, mHollowImageView.dY!!)
        val task = SaveImageTask()
        task.execute(resultBitmap)
    }

    inner class SaveImageTask : AsyncTask<Bitmap, Unit, Unit>() {
        var fileOutput: File? = null
        val dialog = this@CameraActivity.getLoadingDialog(this@CameraActivity,
                R.string.saving_image, false)
        val imagePreviewDialog = ImagePreviewDialog()
        lateinit var bitmap: Bitmap

        override fun onPreExecute() {
            super.onPreExecute()
            val df = SimpleDateFormat("yyyyMMddHHmmss")
            val time = df.format(Date())


            fileOutput = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "MontageCam$time.png")
            dialog.show()
        }

        override fun doInBackground(vararg params: Bitmap?) {
            BitmapUtils.saveBitmap(params[0], fileOutput?.absolutePath)
            bitmap = params[0]!!
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            FileUtil.ablumUpdate(this@CameraActivity, fileOutput?.absolutePath)
            dialog.dismiss()
            imagePreviewDialog.setData(bitmap, this@CameraActivity.resources.getString(
                    R.string.file_has_save_to) , fileOutput?.absolutePath!!)
            imagePreviewDialog.show(this@CameraActivity.fragmentManager, TAG)
        }
    }

    fun startImageEdit(file: File) {
        if (!file.exists()) {
            Toast.makeText(this, resources.getString(R.string.NoImg), Toast.LENGTH_SHORT).show()
        }

        val fileOutput = File(externalCacheDir, "picture_output.png")
        EditImageActivity.start(this, file.absolutePath, fileOutput.absolutePath,
                CALL_EDIT_ACTIVITY)
        finish()
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
                    val ratios = mCameraView.supportedAspectRatios
                    val currentRatio = mCameraView.aspectRatio
                    AspectRatioFragment.newInstance(ratios, currentRatio).show(fragmentManager,
                            FRAGMENT_DIALOG)
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
                mCameraView.facing = if (facing == CameraView.FACING_FRONT) CameraView.FACING_BACK
                else CameraView.FACING_FRONT
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
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
