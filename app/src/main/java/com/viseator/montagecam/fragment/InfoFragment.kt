package com.viseator.montagecam.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.viseator.montagecam.R
import kotlinx.android.synthetic.main.fragment_info.info_main

/**
 * Created by viseator on 11/15/17.
 * Wu Di
 * viseator@gmail.com
 */

/**
 * The info about us
 */
class InfoFragment : Fragment() {


    var listener: View.OnClickListener? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        info_main.setOnClickListener({ v ->
            listener?.onClick(v)
        })
    }
}