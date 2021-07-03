package com.flamyoad.honnoki.cache

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.data.entities.MangaOverview
import com.flamyoad.honnoki.utils.extensions.downloadIntoFile
import java.io.File

// Todo: Try replacing Glide with other libraries to download the image
class CacheManager(private val coverCache: CoverCache, private val context: Context) {

    fun getCoverImage(activity: Activity, imageUrl: String, onLoadComplete: (File) -> Unit) {
        coverCache.get(imageUrl)?.let {
            onLoadComplete(it)
            return
        }

        Glide.with(activity)
            .downloadIntoFile(imageUrl) {
                onLoadComplete(it)
                coverCache.write(it, imageUrl)
            }
    }

    fun getCoverImage(fragment: Fragment, imageUrl: String, onLoadComplete: (File) -> Unit) {
        coverCache.get(imageUrl)?.let {
            onLoadComplete(it)
            return
        }

        Glide.with(fragment)
            .downloadIntoFile(imageUrl) {
                onLoadComplete(it)
                coverCache.write(it, imageUrl)
            }
    }

}