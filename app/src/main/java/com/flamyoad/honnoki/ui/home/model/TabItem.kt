package com.flamyoad.honnoki.ui.home.model

import com.flamyoad.honnoki.data.model.MangaType
import com.flamyoad.honnoki.data.model.Source

data class TabItem(
    val source: Source,
    val type: MangaType
)
