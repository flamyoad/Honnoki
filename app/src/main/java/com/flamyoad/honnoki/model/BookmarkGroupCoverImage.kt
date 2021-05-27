package com.flamyoad.honnoki.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookmark_group_cover_img",
    indices = [androidx.room.Index(value = ["bookmarkGroupId"])],
    foreignKeys = [ForeignKey(
        entity = BookmarkGroup::class,
        parentColumns = ["id"],
        childColumns = ["bookmarkGroupId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )])
data class BookmarkGroupCoverImage(
    @PrimaryKey val id: Long? = null,
    val bookmarkGroupId: Long? = null,
    val coverImage: String,
    val position: Int
)