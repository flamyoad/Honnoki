package com.flamyoad.honnoki.cache

import android.content.Context
import java.io.File

/**
 * Class used to save [com.flamyoad.honnoki.data.entities.MangaOverView.kt]
 * cover image because it will be shown in bookmark and read history sections.
 *
 * We do not care and will not persist cover image for
 * [com.flamyoad.honnoki.data.entities.Manga.kt] because it's only used in
 * paginated list, which will be erased on each refresh anyway.
 */
class CoverCache(private val context: Context) {

    fun getCacheDir(dir: String): File {
        return context.getExternalFilesDir(dir)
            ?: File(context.filesDir, dir).also { it.mkdirs() }
    }

}