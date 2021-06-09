package com.flamyoad.honnoki.data.model

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation

data class BookmarkWithOverview(
    @Embedded val bookmark: Bookmark,

    @Relation(parentColumn = "mangaOverviewId", entityColumn = "id")
    val overview: MangaOverview,

    @Ignore
    val isSelected: Boolean
) {
    constructor(bookmark: Bookmark, overview: MangaOverview) : this(
        bookmark,
        overview,
        false
    )
}