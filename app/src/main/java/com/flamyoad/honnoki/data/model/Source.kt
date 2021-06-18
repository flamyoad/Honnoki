package com.flamyoad.honnoki.data.model

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.flamyoad.honnoki.R

enum class Source(
    val title: String,
    val logo: Int,
    val languageLogo: Int,
    val isEnabled: Boolean = true
) {
    MANGAKALOT("Mangakalot", R.drawable.mangakalot_logo, R.drawable.uk_logo),
    MANGATOWN("MangaTown", R.drawable.mangatown_logo, R.drawable.uk_logo, isEnabled = false),
    READMANGA("ReadManga", R.drawable.readmanga_logo, R.drawable.uk_logo),
    DM5("DM5", R.drawable.dm5_logo, R.drawable.cn_logo),
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