package com.flamyoad.honnoki.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "searched_result")
data class SearchResult(
    @PrimaryKey
    val id: Int? = null,

    val prevKey: Int? = null,
    val nextKey: Int? = null,
    val link: String = "",
    val coverImage: String = "",
    val title: String = "",
    val author: String = "",
    val latestChapter: String
)