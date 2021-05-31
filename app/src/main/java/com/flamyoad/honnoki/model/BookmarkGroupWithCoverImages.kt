package com.flamyoad.honnoki.model

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class BookmarkGroupWithCoverImages(
    @Embedded val bookmarkGroup: BookmarkGroup,

    @ColumnInfo(name = "item_count")
    val itemCount: Int,

    @ColumnInfo(name = "cover_image")
    val coverImage: String?
){

    companion object {
        fun empty() = BookmarkGroupWithCoverImages(
            bookmarkGroup = BookmarkGroup.empty(),
            itemCount = 0,
            coverImage = ""
        )
    }
}