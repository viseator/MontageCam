package com.viseator.montagecam

import android.app.Application
import android.os.StrictMode



/**
 * Created by viseator on 11/17/17.
 * Wu Di
 * viseator@gmail.com
 */

class App :Application(){
    override fun onCreate() {
        super.onCreate()
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }
}