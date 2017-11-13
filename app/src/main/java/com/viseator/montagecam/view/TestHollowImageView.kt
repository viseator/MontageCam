package com.viseator.montagecam.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import com.viseator.montagecam.util.BitmapUtil

/**
 * Created by viseator on 11/13/17.
 * Wu Di
 * viseator@gmail.com
 */
class TestHollowImageView : View {
    val TAG = "@vir TestHollow"

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
            defStyleAttr)

    var mBitmap: Bitmap? = null
        set(value) {
            if (value == null) {
                return
            }
            val metrics = context.resources.displayMetrics
            field = BitmapUtil.bitmapFixToScreen(value, metrics.heightPixels, metrics.widthPixels)
            value.recycle()
        }
    //    var mBitmap: Bitmap? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }
        canvas.save()
        canvas.scale(0.46153846f, 0.46153846f)
        canvas.drawBitmap(mBitmap, 0f, 0f, null)
        canvas.restore()
    }
}
