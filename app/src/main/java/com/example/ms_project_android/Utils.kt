package com.example.ms_project_android

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object Utils {
    fun getAsset(context: Context, filePath: String, override: Boolean = false): File {
        val cachePath = context.cacheDir.absolutePath
        val absoluteFilePath = "$cachePath/$filePath"
        val file = File(absoluteFilePath)
        return if(!file.exists() or override) {
            cacheAsset(context, cachePath, filePath)
        } else {
            file
        }
    }

    private fun cacheAsset(context: Context, cachePath: String, filePath: String): File {
        val pathname = "$cachePath/$filePath"
        val outfile = File(pathname).apply { parentFile?.mkdirs() }

        context.assets.open(filePath).use { inputStream ->
            FileOutputStream(outfile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        Log.d("Utils.CacheAssets", "copy $pathname to $outfile")
        return outfile
    }
}