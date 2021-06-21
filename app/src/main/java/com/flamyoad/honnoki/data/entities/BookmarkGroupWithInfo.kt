package com.flamyoad.honnoki.data.entities

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation

data class BookmarkGroupWithInfo(
    @Embedded val bookmarkGroup: BookmarkGroup,

    @Relation(parentColumn = "id", entityColumn = "bookmarkGroupId")
    val bookmarks: List<Bookmark>,

    @Ignore
    val coverImages: List<String>
) {

    constructor(bookmarkGroup: BookmarkGroup, bookmarks: List<Bookmark>) : this(
        bookmarkGroup,
        bookmarks,
        emptyList()
    )

    val itemCount get() = bookmarks.size

    companion object {
        fun empty() = BookmarkGroupWithInfo(
            bookmarkGroup = BookmarkGroup.empty(),
            bookmarks = emptyList(),
            coverImages = emptyList()
        )
    }
}