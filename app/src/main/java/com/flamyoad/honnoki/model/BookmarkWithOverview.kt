package com.flamyoad.honnoki.model

import androidx.room.Embedded
import androidx.room.Relation

data class BookmarkWithOverview(
    @Embedded val bookmark: Bookmark,

    @Relation(parentColumn = "mangaOverviewId", entityColumn = "id")
    val overview: MangaOverview
)