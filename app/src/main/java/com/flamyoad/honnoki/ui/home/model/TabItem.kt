package com.flamyoad.honnoki.ui.home.model

import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.source.model.TabType

data class TabItem(
    val source: Source,
    val type: TabType,
)
