package com.flamyoad.honnoki.cache

import android.content.Context
import com.flamyoad.honnoki.data.entities.MangaOverview
import com.flamyoad.honnoki.utils.HashUtils
import timber.log.Timber
import java.io.File

/**
 * Class used to save [com.flamyoad.honnoki.data.entities.MangaOverView.kt]
 * cover image because it will be shown in bookmark and read history sections.
 *
 * We do not care and will not persist cover images for
 * [com.flamyoad.honnoki.data.entities.Manga.kt] because it's only used in
 * paginated list, which will be erased on each refresh anyway.
 */
class CoverCache(private val context: Context) {

    private val COVER_DIR = "cover"

    private val cacheDir: File = getCacheDir(COVER_DIR)

    fun get(imageUrl: String): File? {
        val coverImage = File(cacheDir, getCoverIdentifier(imageUrl))
        if (coverImage.exists()) {
            return coverImage
        }
        return null
    }

    fun write(fromGlide: File, imageUrl: String) {
        try {
            val cacheFile = File(cacheDir, getCoverIdentifier(imageUrl))
            cacheFile.outputStream().use {
                fromGlide.inputStream().copyTo(it)
            }
        } catch (e: FileSystemException) {
            Timber.e("Failed to write cover image into cache")
        }
    }

    fun delete(overview: MangaOverview): Boolean {
        val coverImage = File(cacheDir, getCoverIdentifier(overview.coverImage))
        return try {
            coverImage.delete()
        } catch (e: SecurityException) {
            false
        }
    }

    private fun getCacheDir(dir: String): File {
        return context.getExternalFilesDir(dir)
            ?: File(context.filesDir, dir).also { it.mkdirs() }
    }

    fun getCoverIdentifier(imageUrl: String): String {
        val hash = HashUtils.md5(imageUrl)
        return "$hash.png"
    }
}