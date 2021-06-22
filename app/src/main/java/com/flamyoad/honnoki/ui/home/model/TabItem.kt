package com.flamyoad.honnoki.ui.home.model

import com.flamyoad.honnoki.data.entities.MangaType
import com.flamyoad.honnoki.data.Source

data class TabItem(
    val source: Source,
    val type: MangaType
)
