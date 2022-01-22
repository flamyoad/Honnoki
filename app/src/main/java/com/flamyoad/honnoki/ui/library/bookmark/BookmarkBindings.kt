package com.flamyoad.honnoki.ui.library.bookmark

import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.R

object BookmarkBindings {
    @BindingAdapter("toggleSearchLogo")
    fun ImageButton.toggleSearchLogo(isEditing: Boolean) {
        val drawableId = if (isEditing) {
            R.drawable.ic_baseline_close_24_white
        } else {
            R.drawable.ic_search_white_24dp
        }
        Glide.with(this)
            .load(ContextCompat.getDrawable(this.context, drawableId))
            .into(this)
    }
}