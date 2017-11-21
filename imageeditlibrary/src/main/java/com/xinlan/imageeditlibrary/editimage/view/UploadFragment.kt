package com.xinlan.imageeditlibrary.editimage.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.victor.loading.rotate.RotateLoading
import com.xinlan.imageeditlibrary.R
import com.xinlan.imageeditlibrary.editimage.EditImageActivity.BITMAP_FILE
import com.xinlan.imageeditlibrary.editimage.EditImageActivity.INTENT_START_CAMERA_ACTIVITY


/**
 * Created by viseator on 11/15/17.
 * Wu Di
 * viseator@gmail.com
 */
class UploadFragment : Fragment(), View.OnClickListener {

    val TAG = "@vir UploadFragment"
    val label = "TOKEN"
    var progressBar: RotateLoading? = null
    var resultText: TextView? = null
    var mainInfoText: TextView? = null
    var selfButton: ImageView? = null
    var selfText: TextView? = null
    var uploadOk: ImageView? = null
    var shareButton: ImageView? = null
    var shareText: TextView? = null
    var listener: View.OnClickListener? = null
    var temp: Int? = null
    var result: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = activity.layoutInflater.inflate(R.layout.upload_dialog, container, false)
        progressBar = view.findViewById(R.id.upload_progressbar)
        mainInfoText = view.findViewById(R.id.upload_main_text)
        resultText = view.findViewById(R.id.upload_detail_text)
        selfButton = view.findViewById(R.id.token_myself)
        selfText = view.findViewById(R.id.upload_text_myself)
        uploadOk = view.findViewById(R.id.upload_ok)
        shareButton = view.findViewById(R.id.token_share)
        shareText = view.findViewById(R.id.upload_text_share)
        selfButton?.setOnClickListener(this)
        shareButton?.setOnClickListener(this)
        if (temp != null) {
            mainInfoText?.text = getText(temp!!)
            temp = null
        }
        startProgress()
        return view
    }


    override fun onClick(v: View?) {
        if (v == selfButton) {
            listener?.onClick(v)
        } else if (v == shareButton) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.friends))
            intent.putExtra(Intent.EXTRA_TEXT, result)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(Intent.createChooser(intent, getString(R.string.share_to)))
        }
    }

    fun setMainInfo(id: Int) {
        if (mainInfoText == null) {
            temp = id
        } else {
            mainInfoText?.text = getString(id)
        }
    }


    fun startProgress() {
        progressBar?.start()
    }

    fun progressEnd() {
        progressBar?.stop()
        progressBar?.visibility = View.INVISIBLE
        uploadOk?.visibility = View.VISIBLE
        resultText?.visibility = View.VISIBLE
        shareButton?.visibility = View.VISIBLE
        shareText?.visibility = View.VISIBLE
        selfButton?.visibility = View.VISIBLE
        selfText?.visibility = View.VISIBLE
    }

    fun setResultText(result: String) {
        this.result = result
        resultText?.text = SpannableStringBuilder(result)
    }

    fun genShareText(token: String): String {
        return "${activity.resources.getString(
                R.string.share_info_pre)}|$token|${activity.resources.getString(
                R.string.share_info_suf)}"
    }
}