package com.viseator.montagecam.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore


/**
 * Created by viseator on 11/19/17.
 * Wu Di
 * viseator@gmail.com
 */
/**
 * File untils
 */
class FileUtil {
    companion object {
        /**
         * @deprecated
         */
        fun getRealPathFromURI(context: Context, contentURI: Uri): String {
            val result: String

            val cursor = context.contentResolver.query(contentURI,
                    arrayOf(MediaStore.Images.Media.DATA), null, null, null)
            if (cursor == null) {
                // Source is Dropbox or other similar local file path
                result = contentURI.path
            } else {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                result = cursor.getString(idx)
                cursor.close()
            }
            return result
        }
    }
}