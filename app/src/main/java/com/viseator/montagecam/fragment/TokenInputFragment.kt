package com.viseator.montagecam.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.viseator.montagecam.R
import kotlinx.android.synthetic.main.fragment_input.confirm_token
import kotlinx.android.synthetic.main.fragment_input.input_main
import kotlinx.android.synthetic.main.fragment_input.token_input

/**
 * Created by viseator on 11/15/17.
 * Wu Di
 * viseator@gmail.com
 */

/**
 * input dialog result
 */
interface OnInputDialogResultListener {
    fun onResult(result: String)
}

/**
 * Token input Fragment
 */
class TokenInputFragment : Fragment() {
    var resultListener: OnInputDialogResultListener? = null
    var token: String? = null
    var listener: View.OnClickListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_input, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token_input.setOnClickListener({
            token_input.selectAll()
        })
        confirm_token.setOnClickListener({
            resultListener?.onResult(token_input.text.toString())
        })
        input_main.setOnClickListener({ v ->
            listener?.onClick(v)
        })
    }

    override fun onResume() {
        super.onResume()
        token_input.requestFocus()
        if (token != null) {
            token_input.text = SpannableStringBuilder(token)
            token_input.selectAll()
        }
    }
}