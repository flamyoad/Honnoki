package com.flamyoad.honnoki.ui.search.model

import com.flamyoad.honnoki.data.model.Source

data class SearchSource(
    val source: Source,
    val isSelected: Boolean, ) {
    val name get() = source.title
}