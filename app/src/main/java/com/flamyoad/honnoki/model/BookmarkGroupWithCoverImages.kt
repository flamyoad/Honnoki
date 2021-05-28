package com.flamyoad.honnoki.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Relation

data class BookmarkGroupWithCoverImages(
    @Embedded
    val bookmarkGroup: BookmarkGroup,

    @ColumnInfo(name = "item_count")
    val itemCount: Int,

    @Relation(
        parentColumn = "id",
        entityColumn = "bookmarkGroupId"
    )
    val coverImageList: List<BookmarkGroupCoverImage>
) {

    companion object {
        fun empty() = BookmarkGroupWithCoverImages(
            bookmarkGroup = BookmarkGroup(name = ""),
            itemCount = -1,
            coverImageList = emptyList()
        )
    }
}