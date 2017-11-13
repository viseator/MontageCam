package com.viseator.montagecam.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase

/**
 * Created by viseator on 11/12/17.
 * Wu Di
 * viseator@gmail.com
 */

class HollowImageView : ImageViewTouchBase {

    private val TAG = "@vir HollowImageView"
    private var bitmap: Bitmap? = null

    constructor(context: Context) : super(context)


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onDraw(canvas: Canvas) {
        Log.d(TAG, canvas.width.toString() + "x" + canvas.width.toString())
        Log.d(TAG, "bitmap:" + mBitmap.width + "x" + mBitmap.height)
        super.onDraw(canvas)
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888)

        }
    }
}
