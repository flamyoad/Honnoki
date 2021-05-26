package com.flamyoad.honnoki.model

import androidx.room.Embedded
import androidx.room.Relation

data class BookmarkGroupWithCoverImages(
    @Embedded val bookmarkGroup: BookmarkGroup,

    @Relation(
        parentColumn = "id",
        entityColumn = "bookmarkGroupId"
    )
    val coverImageList: List<BookmarkGroupCoverImage>
)