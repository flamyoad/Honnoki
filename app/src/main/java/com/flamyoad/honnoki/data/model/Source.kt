package com.flamyoad.honnoki.data.model

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.flamyoad.honnoki.R

enum class Source(val title: String, val logo: Int, val languageLogo: Int) {
    MANGAKALOT("Mangakalot", R.drawable.mangakalot_logo, R.drawable.uk_logo),
    MANGATOWN("MangaTown", R.drawable.mangakalot_logo, R.drawable.uk_logo),
    SENMANGA("SenManga", R.drawable.senmanga_logo, R.drawable.jp_logo);


    fun getLogoDrawable(context: Context): Drawable? {
        if (logo == -1) return null
        return try {
            ContextCompat.getDrawable(context, logo)
        } catch (e: Exception) {
            null
        }
    }

    fun getLanguageDrawable(context: Context): Drawable? {
        if (languageLogo == -1) return null
        return try {
            ContextCompat.getDrawable(context, languageLogo)
        } catch (e: Exception) {
            null
        }
    }
}