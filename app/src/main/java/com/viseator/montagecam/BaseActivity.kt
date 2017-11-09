package com.viseator.montagecam

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
}