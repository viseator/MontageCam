package com.viseator.montagecam.util

import android.graphics.*
import android.util.Log
import com.viseator.montagecam.entity.BitmapInfo

/**
 * Created by viseator on 11/13/17.
 * Wu Di
 * viseator@gmail.com
 */

class BitmapUtil {
    companion object {
        val TAG = "@vir BitmapUtil"
        val paintColor = Color.WHITE
        fun bitmapFixToScreen(bitmap: Bitmap, h: Int, w: Int): BitmapInfo {
            val height = bitmap.height
            val width = bitmap.width
            Log.d(TAG, "raw:$width x $height")
            val scaleX = w / width.toFloat()
            val scaleY = h / height.toFloat()
            val mainScale = Math.min(scaleX, scaleY)
            Log.d(TAG, "$scaleX : $scaleY - $mainScale")
            val fitX = mainScale == scaleX
            return if (mainScale <= 1) {
                handleScaleIn(bitmap, mainScale, h, w, fitX)
            } else {
                handleScaleOut(bitmap, mainScale, h, w)
            }
        }

        fun handleScaleOut(bitmap: Bitmap, scale: Float, h: Int, w: Int): BitmapInfo {
            val screenBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            screenBitmap.density = bitmap.density
            val canvas = Canvas(screenBitmap)
            val paint = Paint()
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            paint.color = paintColor
            val dX = (screenBitmap.width - bitmap.width) / 2.toFloat()
            val dY = (screenBitmap.height - bitmap.height) / 2.toFloat()
            canvas.drawRect(0f, 0f, screenBitmap.width.toFloat(), dY, paint)
            canvas.drawRect(0f, dY + bitmap.height, screenBitmap.width.toFloat(),
                    screenBitmap.height.toFloat(), paint)
            canvas.drawRect(0f, 0f, dX, screenBitmap.height.toFloat(), paint)
            canvas.drawRect(dX + bitmap.width, 0f, screenBitmap.width.toFloat(),
                    screenBitmap.height.toFloat(), paint)
            canvas.drawBitmap(bitmap, dX, dY, null)
            return BitmapInfo(screenBitmap, scale)
        }

        fun handleScaleIn(bitmap: Bitmap, scale: Float, h: Int, w: Int, fitX: Boolean): BitmapInfo {
            val screenBitmap = if (fitX) {
                Bitmap.createBitmap(bitmap.width,
                        (bitmap.width / w.toFloat() * h.toFloat()).toInt(), Bitmap.Config.ARGB_8888)
            } else {
                Bitmap.createBitmap((bitmap.height / h.toFloat() * w.toFloat()).toInt(),
                        bitmap.height, Bitmap.Config.ARGB_8888)
            }
            screenBitmap.density = bitmap.density
            val canvas = Canvas(screenBitmap)
            val paint = Paint()
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            paint.color = paintColor
            if (fitX) {
                val d = (screenBitmap.height - bitmap.height) / 2.toFloat()
                canvas.drawRect(0f, 0f, screenBitmap.width.toFloat(), d, paint)
                canvas.drawBitmap(bitmap, 0f, d, null)
                canvas.drawRect(0f, d + bitmap.height, screenBitmap.width.toFloat(),
                        screenBitmap.height.toFloat(), paint)
            } else {
                val d = (screenBitmap.width - bitmap.width) / 2.toFloat()
                canvas.drawRect(0f, 0f, d, screenBitmap.height.toFloat(), paint)
                canvas.drawBitmap(bitmap, d, 0f, null)
                canvas.drawRect(d + bitmap.width, 0f, screenBitmap.width.toFloat(),
                        screenBitmap.height.toFloat(), paint)
            }
            Log.d(TAG, "canvas:${canvas.width}x${canvas.height}")
            Log.d(TAG, "bitmap:${bitmap.width}x${bitmap.height}")
            Log.d(TAG, "screenBitmap:${screenBitmap.width}x${screenBitmap.height}")
            return BitmapInfo(screenBitmap, scale)
        }
    }
}