package com.flamyoad.honnoki.common

import androidx.databinding.BindingAdapter
import android.view.View
import androidx.core.view.isVisible

object Bindings {
    @BindingAdapter("viewGone")
    fun View.bindVisibility(visible: Boolean?) {
        isVisible = visible == true
    }
}