package com.flamyoad.honnoki.model

import androidx.room.Entity

@Entity(tableName = "manga_overview")
data class MangaOverview(
    val id: Long? = null,
    val coverImage: String,
    val title: String,
    val source: Source,
    val link: String
)