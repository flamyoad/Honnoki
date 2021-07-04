package com.flamyoad.honnoki.ui.search.model

import com.flamyoad.honnoki.data.GenreConstants

data class SearchGenre(
    val name: String,
    val genre: GenreConstants,
    val isSelected: Boolean
)
