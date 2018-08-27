package com.viseator.montagecam.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.viseator.montagecam.R
import kotlinx.android.synthetic.main.fragment_compress_resut.result_detail_text
import kotlinx.android.synthetic.main.fragment_compress_resut.result_image
import kotlinx.android.synthetic.main.fragment_compress_resut.result_main_text
import kotlinx.android.synthetic.main.fragment_compress_resut.result_progressBar
import kotlinx.android.synthetic.main.fragment_compress_resut.result_share
import org.jetbrains.anko.imageBitmap
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

    var path: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_compress_resut, container, false)
        ButterKnife.bind(this, view!!)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        result_share.setOnClickListener(this)
        result_progressBar.start()
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
        result_progressBar.stop()
        this.path = path
        result_main_text.visibility = View.GONE
        result_image.imageBitmap = bitmap
        result_image.visibility = View.VISIBLE
        result_share.visibility = View.VISIBLE
        result_detail_text.text = "$info$path"
    }
}