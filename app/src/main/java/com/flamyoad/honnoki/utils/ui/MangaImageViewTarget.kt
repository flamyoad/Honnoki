package com.flamyoad.honnoki.utils.ui

import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.core.view.isVisible
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.flamyoad.honnoki.databinding.FragmentImageBinding
import com.flamyoad.honnoki.databinding.ReaderImageListItemBinding
import java.io.File

class MangaImageViewTarget(val binding: ReaderImageListItemBinding)
    : CustomViewTarget<SubsamplingScaleImageView, File>(binding.imageView) {
    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
        val uri = ImageSource.uri(Uri.fromFile(resource))
        view.setImage(uri)
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        binding.progressBarContainer.isVisible = false
        binding.errorContainer.isVisible = true
    }

    override fun onResourceCleared(placeholder: Drawable?) {}
}

class MangaImageFragmentViewTarget(val binding: FragmentImageBinding)
    : CustomViewTarget<SubsamplingScaleImageView, File>(binding.imageView) {
    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
        val uri = ImageSource.uri(Uri.fromFile(resource))
        view.setImage(uri)
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        binding.progressBarContainer.isVisible = false
        binding.errorContainer.isVisible = true
    }

    override fun onResourceCleared(placeholder: Drawable?) {}
}