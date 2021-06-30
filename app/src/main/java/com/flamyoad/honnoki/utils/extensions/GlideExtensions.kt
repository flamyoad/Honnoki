package com.flamyoad.honnoki.utils.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.io.File

fun RequestManager.downloadIntoFile(
    imageUrl: String,
    timeout: Int = 10000,
    successCallback: (File) -> Unit
) {
    this.downloadOnly()
        .timeout(timeout)
        .load(imageUrl)
        .into(object : CustomTarget<File>() {
            override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                successCallback.invoke(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }
        })
}