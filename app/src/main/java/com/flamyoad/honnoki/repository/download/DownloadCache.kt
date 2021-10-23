package com.flamyoad.honnoki.repository.download

import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.io.File
import java.io.IOException

class DownloadCache(private val context: Context) {

    private val DOWNLOADS_DIR = "downloads"

    private val downloadDir: File = getDirectory(DOWNLOADS_DIR)

    fun write(file: File): Boolean {
        try {
            return true
        } catch (e: IOException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            return false
        }
    }

    private fun getDirectory(dir: String): File {
        return context.getExternalFilesDir(dir)
            ?: File(context.filesDir, dir).also { it.mkdirs() }
    }
}