package com.flamyoad.honnoki.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookmark_group",
    indices = [androidx.room.Index(value = ["name"], unique = true)],
)
data class BookmarkGroup(
    @PrimaryKey val id: Long? = null,
    val name: String,
) {
    companion object {
        fun empty() = BookmarkGroup(name = "")
    }
}