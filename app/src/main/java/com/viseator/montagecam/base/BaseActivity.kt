package com.viseator.montagecam.base

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import butterknife.ButterKnife

/**
 * Created by viseator on 11/9/17.
 * Wu Di
 * viseator@gmail.com
 */

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
        init()
        initView()
    }

    abstract fun init()
    abstract fun initView()
    protected fun setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    fun getLoadingDialog(context: Context, titleId: Int,
                         canCancel: Boolean): Dialog = getLoadingDialog(context,
            context.getString(titleId), canCancel)


    fun getLoadingDialog(context: Context, title: String, canCancel: Boolean): Dialog {
        val dialog = ProgressDialog(context)
        dialog.setCancelable(canCancel)
        dialog.setMessage(title)
        return dialog
    }
}