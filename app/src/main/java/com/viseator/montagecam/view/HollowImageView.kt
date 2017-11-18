package com.viseator.montagecam.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.viseator.montagecam.util.BitmapUtil

/**
 * Created by viseator on 11/13/17.
 * Wu Di
 * viseator@gmail.com
 */
data class BitmapInfo(val bitmap: Bitmap, val scale: Float, val dX: Float, val dY: Float)

class HollowImageView : View {
    val TAG = "@vir TestHollow"

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
            defStyleAttr)

    private val mPaint = Paint()
    var paintAlpha: Int = 255
        set(value) {
            if (value > 255) field = 255 else if (value < 0) field = 0 else field = value
            mPaint.alpha = field
            invalidate()
        }
    var dX: Float? = null
    var dY: Float? = null
    var scale: Float? = null
    var bitmap: Bitmap? = null
        set(value) {
            if (value == null) {
                return
            }
            val metrics = context.resources.displayMetrics
            val bitmapInfo = BitmapUtil.bitmapFixToScreen(value, metrics.heightPixels,
                    metrics.widthPixels)
            field = bitmapInfo.bitmap
            scale = bitmapInfo.scale
            dX = bitmapInfo.dX
            dY = bitmapInfo.dY
            value.recycle()
        }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }
        canvas.save()
        if (scale!! < 1) {
            canvas.scale(scale!!, scale!!)
        }

        canvas.drawBitmap(bitmap, 0f, 0f, mPaint)
        canvas.restore()
    }
}
