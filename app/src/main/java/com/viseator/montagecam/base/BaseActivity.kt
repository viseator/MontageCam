package com.viseator.montagecam.base

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import butterknife.ButterKnife
import com.viseator.montagecam.R

/**
 * Created by viseator on 11/9/17.
 * Wu Di
 * viseator@gmail.com
 */

/**
 * Base activity
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
    /**
     * set activity enter fullscreen state
     */
    protected fun setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    /**
     * get simple loading dialog
     * @param context
     * @param titleId the resource id for title
     * @param canCancel can dialog be canceled
     */
    fun getLoadingDialog(context: Context, titleId: Int,
                         canCancel: Boolean): Dialog = getLoadingDialog(context,
            context.getString(titleId), canCancel)


    fun getLoadingDialog(context: Context, title: String, canCancel: Boolean): Dialog {
        val dialog = ProgressDialog(context)
        dialog.setCancelable(canCancel)
        dialog.setMessage(title)
        return dialog
    }

    /**
     * show snack bar
     * @param viewId the id for the view show snack bar on
     * @param stringId the resource id for the string show in snack bar
     * @author viseator
     */
    fun showSnackBar(viewId: Int, stringId: Int) {
        val snackBar: Snackbar = Snackbar.make(findViewById(viewId), resources.getString(stringId),
                Snackbar.LENGTH_SHORT)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(Color.WHITE)
        val textView: TextView = snackBarView.findViewById(
                android.support.design.R.id.snackbar_text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextColor(getColor(R.color.moca_gra_blue))
        } else {
            textView.setTextColor(resources.getColor(R.color.moca_gra_blue))
        }
        snackBar.show()
    }
}