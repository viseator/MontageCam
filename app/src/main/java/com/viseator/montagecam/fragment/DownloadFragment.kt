package com.viseator.montagecam.fragment

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView
import com.viseator.montagecam.R

/**
 * Created by viseator on 11/15/17.
 * Wu Di
 * viseator@gmail.com
 */

class DownloadFragment : Fragment() {

    @BindView(R.id.download_progress) lateinit var progress: AnimatedCircleLoadingView
    @BindView(R.id.download_text) lateinit var text: TextView
    @BindView(R.id.download_main) lateinit var background: ConstraintLayout
    var failed = false

    var listener: View.OnClickListener? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_download, container, false)
        ButterKnife.bind(this, view)
        background.setOnClickListener({ v ->
            listener?.onClick(v)
        })
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progress.startDeterminate()
    }

    fun setDownloadProgress(data: Int) {
        progress.setPercent(data)
    }

    fun fail() {
        failed = true
        text.text = getText(R.string.download_error)
        progress.stopFailure()
    }

    fun restart() {
        text.text = getText(R.string.downloading)
        failed = false
        progress.resetLoading()
    }
}