package com.easyrent.abccarapp.abccar.tools

import android.content.Context
import android.net.Uri
import android.provider.MediaStore.Images
import org.w3c.dom.Document
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class FilesTool {

    // 因為使用Photo Picker無法直接取得檔案，因此需要使用FileStream進行轉換
    fun parseFileToAbsPath(uri: Uri, extension: String, context: Context): File {
        // 建立一個檔案
        val file = File(context.cacheDir, "${System.currentTimeMillis()}.$extension")
        val contentResolver = context.contentResolver

        uri.path?.let { _ ->
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val outputStream: OutputStream = FileOutputStream(file)
                val buf = ByteArray (102400)
                var len: Int

                inputStream?.let { inputS ->
                    while (inputS.read (buf).also { len = it } > 0) outputStream.write(buf, 0, len)
                    outputStream.close ()
                    inputStream.close ()
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return file
    }

}