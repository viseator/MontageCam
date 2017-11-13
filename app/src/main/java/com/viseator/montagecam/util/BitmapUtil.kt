package com.viseator.montagecam.util

import android.graphics.*
import android.util.Log

/**
 * Created by viseator on 11/13/17.
 * Wu Di
 * viseator@gmail.com
 */

class BitmapUtil {
    companion object {
        val TAG = "@vir BitmapUtil"
        fun bitmapFixToScreen(bitmap: Bitmap, h: Int, w: Int): Bitmap {
            val height = bitmap.height
            val width = bitmap.width
            Log.d(TAG, "raw:$width x $height")
            val scaleX = w / width.toFloat()
            val scaleY = h / height.toFloat()
            val mainScale = Math.min(scaleX, scaleY)
            Log.d(TAG, "$scaleX : $scaleY - $mainScale")
            val fitX = mainScale == scaleX
            if (mainScale <= 1) {
                return handleScaleIn(bitmap, mainScale, h, w, fitX)
            }
            return bitmap
        }

        fun handleScaleIn(bitmap: Bitmap, scale: Float, h: Int, w: Int, fitX: Boolean): Bitmap {
            val screenBitmap = if (fitX) {
                Bitmap.createBitmap(bitmap.width,
                        (bitmap.width / w.toFloat() * h.toFloat()).toInt(), Bitmap.Config.ARGB_8888)
            } else {
                Bitmap.createBitmap((bitmap.height / h.toFloat() * w.toFloat()).toInt(),
                        bitmap.height, Bitmap.Config.ARGB_8888)
            }
            val canvas = Canvas(screenBitmap)
            val paint = Paint()
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            paint.color = Color.BLACK
            val delta: Float = if (fitX) {
                val d = (screenBitmap.height - bitmap.height) / 2.toFloat()
                canvas.drawRect(0f, 0f, screenBitmap.width.toFloat(), d, paint)
                canvas.drawRect(0f, d + bitmap.height, screenBitmap.width.toFloat(),
                        screenBitmap.height.toFloat(), paint)
                d
            } else {
                val d = (screenBitmap.width - bitmap.width) / 2.toFloat()
                canvas.drawRect(0f, 0f, d, screenBitmap.height.toFloat(), paint)
                canvas.drawRect(d + bitmap.width, 0f, screenBitmap.width.toFloat(),
                        screenBitmap.height.toFloat(), paint)
                d
            }
            if (fitX) {
                canvas.translate(0f, delta)
            } else {
                canvas.translate(delta, 0f)
            }
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            Log.d(TAG, "canvas:${canvas.width}x${canvas.height}")
            Log.d(TAG, "bitmap:${bitmap.width}x${bitmap.height}")
            Log.d(TAG, "screenBitmap:${screenBitmap.width}x${screenBitmap.height}")
            return screenBitmap
        }
    }
}