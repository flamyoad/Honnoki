package com.flamyoad.honnoki.model

import androidx.room.Entity

@Entity(tableName = "authors")
data class Author(
    val id: Long? = null,
    val name: String,
    val link: String
)