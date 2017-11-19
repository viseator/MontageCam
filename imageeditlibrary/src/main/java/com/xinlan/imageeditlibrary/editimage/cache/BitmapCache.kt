package com.xinlan.imageeditlibrary.editimage.cache

import android.graphics.Bitmap
import android.util.Log

/**
 * Created by viseator on 11/18/17.
 * Wu Di
 * viseator@gmail.com
 */

class BitmapCache {
    interface CacheStateChangeListener {
        fun canUndo(b: Boolean)
        fun canRedo(b: Boolean)
    }

    val TAG = "@vir BitmapCache"

    val CACHE_SIZE = 5
    val cacheBitmap = ArrayList<Bitmap>()
    var currentPos = -1
    var listener: CacheStateChangeListener? = null

    fun push(bitmap: Bitmap) {
        Log.d(TAG, "push bitmap : ${bitmap.density} ${bitmap.width} x ${bitmap.height}")
        if (currentPos == -1) {
            cacheBitmap.add(bitmap)
            ++currentPos
            listener?.canUndo(false)
            listener?.canRedo(false)
            return
        }
        if (currentPos != cacheBitmap.size - 1) {
            // not the last, delete after and push here
            while (cacheBitmap.size - 1 != currentPos) {
                cacheBitmap.removeAt(currentPos + 1).recycle()
            }
            cacheBitmap.add(bitmap)
            ++currentPos
        } else {
            // is the last
            if (cacheBitmap.size == CACHE_SIZE) {
                // is full, remove first
                cacheBitmap.removeAt(0).recycle()
                --currentPos
            }
            cacheBitmap.add(bitmap)
            ++currentPos
        }
        listener?.canUndo(true)
        listener?.canRedo(false)
    }

    fun canUndo() = currentPos > 0
    fun canRedo() = currentPos != -1 && currentPos != cacheBitmap.size - 1
    fun undo(): Bitmap? {
        if (canUndo()) {
            --currentPos
            listener?.canUndo(canUndo())
            listener?.canRedo(true)
            return cacheBitmap[currentPos]
        }
        return null
    }

    fun redo(): Bitmap? {
        if (canRedo()) {
            ++currentPos
            listener?.canUndo(true)
            listener?.canRedo(canRedo())
            return cacheBitmap[currentPos]
        }
        return null
    }

    fun reset() {
        while (cacheBitmap.size != 0) {
            cacheBitmap.removeAt(0).recycle()
        }
        currentPos = -1
    }

}