package com.viseator.montagecam.view

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import com.viseator.montagecam.R

/**
 * Created by viseator on 11/15/17.
 * Wu Di
 * viseator@gmail.com
 */
interface OnInputDialogResultListener {
    fun onResult(result: String)
}

class InputDialog : DialogFragment() {
    var resultListener: OnInputDialogResultListener? = null

    @BindView(R.id.token_input) lateinit var editText: EditText
    @BindView(R.id.confirm_token) lateinit var button: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_input, null)
        ButterKnife.bind(this, view)
        val builder = AlertDialog.Builder(activity).setTitle(R.string.input_token).setView(view)
        button.setOnClickListener({
            resultListener?.onResult(editText.text.toString())
        })
        return builder.create()
    }
}