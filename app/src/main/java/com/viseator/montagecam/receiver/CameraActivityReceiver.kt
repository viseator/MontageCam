package com.viseator.montagecam.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.viseator.montagecam.activity.CameraActivity
import com.viseator.montagecam.activity.MainActivity

/**
 * Created by viseator on 11/20/17.
 * Wu Di
 * viseator@gmail.com
 */

/**
 * Local receiver to proxy the intent from edit activity
 */
class CameraActivityReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val newIntent = Intent(context, CameraActivity::class.java)
        newIntent.putExtra(MainActivity.BITMAP_FILE,
                intent?.getStringExtra(MainActivity.BITMAP_FILE))
        context?.startActivity(newIntent)
    }

}