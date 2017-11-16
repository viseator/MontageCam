package com.viseator.montagecam.view

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.viseator.montagecam.R
import org.jetbrains.anko.imageBitmap
import android.content.Intent
import android.net.Uri
import org.jetbrains.anko.startActivity


/**
 * Created by viseator on 11/16/17.
 * Wu Di
 * viseator@gmail.com
 */

class ImagePreviewDialog : DialogFragment() {
    @BindView(R.id.dialog_image_preview) lateinit var imageView: ImageView
    lateinit var mView: View
    @BindView(R.id.dialog_info_view) lateinit var textView: TextView
    lateinit var mBitmap: Bitmap
    lateinit var mText: String
    lateinit var mPath: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = activity.layoutInflater.inflate(R.layout.dialog_image_preview, null)
        ButterKnife.bind(this, mView)
        imageView.setImageBitmap(mBitmap)
        imageView.setOnClickListener({
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.setDataAndType(Uri.parse("file://" + mPath), "image/*")
            startActivity(intent)
        })
        textView.text = mText
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity).setCustomTitle(null).setView(
                mView).setPositiveButton(R.string.return_, { _: DialogInterface, _: Int ->
            activity.finish()
        })
        return builder.create()
    }

    fun setData(bitmap: Bitmap, info: String, path: String) {
        mBitmap = bitmap
        mText = info + path
        mPath = path
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        activity.finish()
    }
}