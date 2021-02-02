package com.flamyoad.honnoki.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manga")
data class Manga(
    @PrimaryKey val id: Long? = null,
    val coverImage: String,
    val title: String,
    val latestChapter: String,
    val viewCount: Int,
    val link: String,
    val source: Source,
    val type: MangaType,
    val prevKey: Int? = null,
    val nextKey: Int? = null
)