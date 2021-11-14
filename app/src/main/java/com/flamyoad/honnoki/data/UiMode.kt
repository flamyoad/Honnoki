package com.flamyoad.honnoki.data

import android.content.Context
import androidx.core.content.ContextCompat
import com.flamyoad.honnoki.R

enum class UiMode(val stringId: Int, val drawableId: Int) {
    SYSTEM_DEFAULT(
        R.string.ui_mode_system_default,
        R.drawable.day_mode_screenshot
    ),
    LIGHT(
        R.string.ui_mode_light,
        R.drawable.day_mode_screenshot
    ),
    DARK(
        R.string.ui_mode_dark,
        R.drawable.night_mode_screenshot
    );

    fun getString(context: Context) = context.getString(stringId)
    fun getDrawable(context: Context) =
        ContextCompat.getDrawable(context, drawableId)
}