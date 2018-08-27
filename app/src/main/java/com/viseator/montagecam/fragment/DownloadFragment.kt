package com.viseator.montagecam.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.viseator.montagecam.R
import kotlinx.android.synthetic.main.fragment_download.download_main
import kotlinx.android.synthetic.main.fragment_download.download_progress
import kotlinx.android.synthetic.main.fragment_download.download_text

/**
 * Created by viseator on 11/15/17.
 * Wu Di
 * viseator@gmail.com
 */

class DownloadFragment : Fragment() {

    var failed = false

    var listener: View.OnClickListener? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_download, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        download_main.setOnClickListener({ v ->
            listener?.onClick(v)
        })
        download_progress.startDeterminate()
    }

    fun setDownloadProgress(data: Int) {
        download_progress.setPercent(data)
    }

    fun fail() {
        failed = true
        download_text.text = getText(R.string.download_error)
        download_progress.stopFailure()
    }

    fun restart() {
        download_text.text = getText(R.string.downloading)
        failed = false
        download_progress.resetLoading()
    }
}