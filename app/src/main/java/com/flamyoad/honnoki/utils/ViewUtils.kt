package com.flamyoad.honnoki.utils

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.flamyoad.honnoki.R
import android.view.View
import android.view.inputmethod.InputMethodManager

object ViewUtils {
    fun getLoadingIndicator(context: Context): Drawable {
        val ringColor = if (isNightModeEnabled(context)) {
            Color.WHITE
        } else {
            R.color.colorPrimary
        }

        val circularProgressDrawable = CircularProgressDrawable(context).apply {
            strokeWidth = 10f
            centerRadius = 75f
            setColorSchemeColors(ringColor)
        }
        circularProgressDrawable.start()
        return circularProgressDrawable
    }

    /**
     * Value returned:
     * Configuration.UI_MODE_NIGHT_YES,
     * Configuration.UI_MODE_NIGHT_NO,
     * Configuration.UI_MODE_NIGHT_UNDEFINED
     */
    fun getUiMode(context: Context): Int {
        return context.resources.configuration.uiMode and (Configuration.UI_MODE_NIGHT_MASK)
    }

    fun isNightModeEnabled(context: Context): Boolean {
        return getUiMode(context) == Configuration.UI_MODE_NIGHT_YES
    }

    fun View.showKeyboard() {
        this.isFocusableInTouchMode = true
        this.requestFocus()
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    fun View.hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
}