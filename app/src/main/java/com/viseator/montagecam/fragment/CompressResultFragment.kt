package com.viseator.montagecam.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.victor.loading.rotate.RotateLoading
import com.viseator.montagecam.R
import org.jetbrains.anko.imageBitmap
import android.content.Intent
import android.net.Uri
import java.io.File


/**
 * Created by viseator on 11/22/17.
 * Wu Di
 * viseator@gmail.com
 */

/**
 * Info fragment show the result of compress
 */
class CompressResultFragment : Fragment(), View.OnClickListener {

    @BindView(R.id.result_image) lateinit var resultImage: ImageView
    @BindView(R.id.result_progressBar) lateinit var progressBar: RotateLoading
    @BindView(R.id.result_main_text) lateinit var mainInfo: TextView
    @BindView(R.id.result_detail_text) lateinit var detailText: TextView
    @BindView(R.id.result_share) lateinit var shareButton: ImageView
    var path: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_compress_resut, container, false)
        ButterKnife.bind(this, view!!)
        shareButton.setOnClickListener(this)
        progressBar.start()
        return view
    }

    override fun onClick(v: View?) {
        val file = File(path)
        val imageUri = Uri.fromFile(file)
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        shareIntent.type = "image/*"
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)))
    }

    fun showImage(bitmap: Bitmap, info: String, path: String) {
        progressBar.stop()
        this.path = path
        mainInfo.visibility = View.GONE
        resultImage.imageBitmap = bitmap
        resultImage.visibility = View.VISIBLE
        shareButton.visibility = View.VISIBLE
        detailText.text = "$info$path"
    }
}