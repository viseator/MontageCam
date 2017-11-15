package com.xinlan.imageeditlibrary.editimage.view

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.View
import com.xinlan.imageeditlibrary.R
import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.widget.*


/**
 * Created by viseator on 11/15/17.
 * Wu Di
 * viseator@gmail.com
 */
class UploadDialog : DialogFragment(), View.OnClickListener {

    val label = "TOKEN"
    var progressBar: ProgressBar? = null
    var resultText: EditText? = null
    var titleText: TextView? = null
    var button: Button? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity.layoutInflater.inflate(R.layout.upload_dialog, null)
        val headerView = activity.layoutInflater.inflate(R.layout.upload_dialog_header, null)
        titleText = headerView.findViewById(R.id.upload_dialog_header_text)
        setTitle(activity.resources.getString(R.string.uploading))
        progressBar = view.findViewById(R.id.upload_progressbar)
        resultText = view.findViewById(R.id.upload_result_text)
        button = view.findViewById(R.id.copy_token_button)
        button?.setOnClickListener(this)
        val builder = AlertDialog.Builder(activity)

        builder.setCustomTitle(headerView).setCancelable(false).setView(view)
        return builder.create()
    }

    override fun onClick(v: View?) {
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, resultText?.text)
        clipboard.primaryClip = clip
        Toast.makeText(activity, R.string.copy_success, Toast.LENGTH_LONG).show()
        isCancelable = true
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
        button?.visibility = View.VISIBLE
    }

    fun setResultText(result: String) {
        resultText?.text = SpannableStringBuilder(result)
        resultText?.selectAll()
    }
}