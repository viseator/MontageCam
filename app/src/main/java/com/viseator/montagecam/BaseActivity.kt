package com.viseator.montagecam

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import butterknife.ButterKnife

/**
 * Created by viseator on 11/9/17.
 * Wu Di
 * viseator@gmail.com
 */

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
        ButterKnife.bind(this)
        init()
        initView()
    }

    abstract fun init()
    abstract fun initView()
    abstract fun getLayout(): Int
    protected fun setFullScreen() {
        val decorView: View = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
    }
}