package com.flamyoad.honnoki.model

import androidx.room.*

@Entity(
    tableName = "bookmark",
    indices = [Index(value = ["bookmarkGroupId", "mangaOverviewId"])],
    foreignKeys = [ForeignKey(
        entity = BookmarkGroup::class,
        parentColumns = ["id"],
        childColumns = ["bookmarkGroupId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Bookmark(
    @PrimaryKey val id: Long? = null,
    val bookmarkGroupId: Long,
    val mangaOverviewId: Long,
)