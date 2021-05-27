package com.flamyoad.honnoki.model

import androidx.room.*

@Entity(
    tableName = "bookmark_group",
    indices = [Index(value = ["name"], unique = true)],
)
data class BookmarkGroup(
    @PrimaryKey val id: Long? = null,
    val name: String,

    @Ignore val isSelected: Boolean = false
) {

    constructor(id: Long, name: String): this(id, name, false)

    companion object {
        fun empty() = BookmarkGroup(name = "")
    }
}