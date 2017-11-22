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
class InfoFragment : Fragment() {

    @BindView(R.id.info_main) lateinit var background: ConstraintLayout

    var listener: View.OnClickListener? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)
        ButterKnife.bind(this, view)
        background.setOnClickListener({ v ->
            listener?.onClick(v)
        })
        return view
    }
}