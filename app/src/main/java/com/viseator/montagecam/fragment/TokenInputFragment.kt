package com.viseator.montagecam.fragment

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
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

class TokenInputFragment : Fragment() {
    var resultListener: OnInputDialogResultListener? = null
    var token: String? = null
    var listener: View.OnClickListener? = null

    @BindView(R.id.token_input) lateinit var editText: EditText
    @BindView(R.id.confirm_token) lateinit var button: ImageView
    @BindView(R.id.input_main) lateinit var background: ConstraintLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_input, container, false)
        ButterKnife.bind(this, view)
        if (token != null) {
            editText.text = SpannableStringBuilder(token)
            editText.selectAll()
        }
        editText.setOnClickListener({
            editText.selectAll()
        })
        button.setOnClickListener({
            resultListener?.onResult(editText.text.toString())
        })
        background.setOnClickListener({ v ->
            listener?.onClick(v)
        })
        return view
    }

    override fun onResume() {
        super.onResume()
        editText.requestFocus()
    }
}