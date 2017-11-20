package com.xinlan.imageeditlibrary.editimage.view

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.xinlan.imageeditlibrary.R


/**
 * Created by viseator on 11/15/17.
 * Wu Di
 * viseator@gmail.com
 */
class UploadDialog : DialogFragment(), View.OnClickListener {

    val TAG = "@vir UploadDialog"
    val label = "TOKEN"
    var progressBar: ProgressBar? = null
    var resultText: TextView? = null
    var titleText: TextView? = null
    var selfButton: Button? = null
    var listener: View.OnClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity.layoutInflater.inflate(R.layout.upload_dialog, null)
        val headerView = activity.layoutInflater.inflate(R.layout.upload_dialog_header, null)
        titleText = headerView.findViewById(R.id.upload_dialog_header_text)
        setTitle(activity.resources.getString(R.string.uploading))
        progressBar = view.findViewById(R.id.upload_progressbar)
        progressBar?.max = 100
        resultText = view.findViewById(R.id.upload_result_text)
        selfButton = view.findViewById(R.id.token_use_myself)
        selfButton?.setOnClickListener(this)
        val builder = AlertDialog.Builder(activity)

        builder.setCustomTitle(headerView).setView(view)
        val result = builder.create()
        result.setCanceledOnTouchOutside(false)
        return result
    }

    override fun onClick(v: View?) {
        listener?.onClick(v)
    }

    fun setTitle(s: String) {
        titleText?.text = s
    }

    fun setProgress(progress: Int) {
        progressBar?.progress = progress
    }

    fun progressEnd() {
        progressBar?.visibility = View.GONE
        resultText?.visibility = View.VISIBLE
        selfButton?.visibility = View.VISIBLE
    }

    fun setResultText(result: String) {
        resultText?.text = SpannableStringBuilder(result)
        isCancelable = true
    }

    fun genShareText(token: String): String {
        return "${activity.resources.getString(
                R.string.share_info_pre)}|$token|${activity.resources.getString(
                R.string.share_info_suf)}"
    }
}