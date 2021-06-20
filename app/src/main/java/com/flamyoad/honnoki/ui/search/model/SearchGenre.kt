package com.flamyoad.honnoki.ui.search.model

import android.content.Context
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.data.GenreConstants

data class SearchGenre(
    val name: String,
    val genre: GenreConstants,
    val isSelected: Boolean)
