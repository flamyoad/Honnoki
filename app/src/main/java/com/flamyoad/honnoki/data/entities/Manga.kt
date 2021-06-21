package com.flamyoad.honnoki.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "manga",
    indices = [Index(value = ["title", "source", "type"], unique = true)]
)
data class Manga(
    @PrimaryKey
    val id: Long? = null,
    val coverImage: String = "",
    val title: String = "",
    val latestChapter: String = "",
    val viewCount: Int = -1,
    val link: String = "",
    val source: Source,
    val type: MangaType,
    val prevKey: Int? = null,
    val nextKey: Int? = null
)