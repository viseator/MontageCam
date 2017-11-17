package com.viseator.montagecam.util

import android.graphics.*
import android.util.Log
import com.viseator.montagecam.view.BitmapInfo

/**
 * Created by viseator on 11/13/17.
 * Wu Di
 * viseator@gmail.com
 */

class BitmapUtil {
    companion object {
        val TAG = "@vir BitmapUtil"
        val paintColor = Color.BLACK
        val paintAlpha = 180


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
            paint.alpha = paintAlpha
            val dX = (screenBitmap.width - bitmap.width) / 2.toFloat()
            val dY = (screenBitmap.height - bitmap.height) / 2.toFloat()
            canvas.drawRect(0f, 0f, screenBitmap.width.toFloat(), dY, paint)
            canvas.drawRect(0f, dY + bitmap.height, screenBitmap.width.toFloat(),
                    screenBitmap.height.toFloat(), paint)
            canvas.drawRect(0f, dY, dX, screenBitmap.height.toFloat() - dY, paint)
            canvas.drawRect(dX + bitmap.width, dY, screenBitmap.width.toFloat(),
                    screenBitmap.height.toFloat() - dY, paint)
            canvas.drawBitmap(bitmap, dX, dY, null)
            return BitmapInfo(screenBitmap, scale, dX, dY)
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
            paint.alpha = paintAlpha

            val delta: Float = if (fitX) {
                val d = (screenBitmap.height - bitmap.height) / 2.toFloat()
                canvas.drawRect(0f, 0f, screenBitmap.width.toFloat(), d, paint)
                canvas.drawBitmap(bitmap, 0f, d, null)
                canvas.drawRect(0f, d + bitmap.height, screenBitmap.width.toFloat(),
                        screenBitmap.height.toFloat(), paint)
                d
            } else {
                val d = (screenBitmap.width - bitmap.width) / 2.toFloat()
                canvas.drawRect(0f, 0f, d, screenBitmap.height.toFloat(), paint)
                canvas.drawBitmap(bitmap, d, 0f, null)
                canvas.drawRect(d + bitmap.width, 0f, screenBitmap.width.toFloat(),
                        screenBitmap.height.toFloat(), paint)
                d
            }
            Log.d(TAG, "afterScaleIn:${screenBitmap.width}x${screenBitmap.height}")
            return if (fitX) {
                BitmapInfo(screenBitmap, scale, 0f, delta)
            } else {
                BitmapInfo(screenBitmap, scale, delta, 0f)
            }
        }

        fun composeBitmap(bgImg: Bitmap, fgImg: Bitmap, w: Int, h: Int, fgScale: Float, dX: Float,
                          dY: Float): Bitmap {
            val bgInfo = bitmapFixToScreen(bgImg, h, w)
            bgImg.recycle()
            val bgScale = bgInfo.scale
            val deltaScale = fgImg.width / bgInfo.bitmap.width.toFloat()
            Log.d(TAG, "fgImg: ${fgImg.width}x${fgImg.height}")
            Log.d(TAG, "deltaScale: $deltaScale")
            var result = Bitmap.createBitmap(fgImg.width, fgImg.height, Bitmap.Config.ARGB_8888)
            Log.d(TAG, "result:${result.width}x${result.height}")
            Log.d(TAG, "delta: $dX $dY")
            val canvas = Canvas(result)
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            canvas.save()
            canvas.scale(deltaScale, deltaScale)
            canvas.drawBitmap(bgInfo.bitmap, 0f, 0f, null)
            canvas.restore()

            canvas.save()
            //            canvas.scale(fgScale, fgScale)
            val paint = Paint()
            //            paint.alpha = 50
            canvas.drawBitmap(fgImg, 0f, 0f, paint)
            canvas.restore()
            bgInfo.bitmap.recycle()
            result = resizeBitmap(result, dX, dY)
            return result
        }

        fun resizeBitmap(src: Bitmap, dX: Float, dY: Float): Bitmap {
            if (dX == 0f && dY == 0f) {
                return src
            }
            val result = Bitmap.createBitmap(src.width - 2 * dX.toInt(),
                    src.height - 2 * dY.toInt(), Bitmap.Config.ARGB_8888)
            val canvas = Canvas(result)
            canvas.drawBitmap(src, -dX, -dY, null)
            src.recycle()
            return result
        }
    }
}