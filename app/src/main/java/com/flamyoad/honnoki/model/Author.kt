package com.flamyoad.honnoki.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "authors")
data class Author(
    @PrimaryKey val id: Long? = null,
    val name: String,
    val link: String
)