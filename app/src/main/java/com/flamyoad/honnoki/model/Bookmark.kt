package com.flamyoad.honnoki.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookmark",
    indices = [Index(value = ["bookmarkGroupId"]), Index(value = ["mangaOverviewId"], unique = true)],
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