package com.flamyoad.honnoki.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

object ViewUtils {
    fun getLoadingIndicator(context: Context): Drawable {
        val circularProgressDrawable = CircularProgressDrawable(context).apply {
            strokeWidth = 5f
            centerRadius = 75f
        }
        circularProgressDrawable.start()
        return circularProgressDrawable
    }
}