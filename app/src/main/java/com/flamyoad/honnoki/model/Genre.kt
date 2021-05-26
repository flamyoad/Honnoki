package com.flamyoad.honnoki.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genres")
data class Genre(
    @PrimaryKey val id: Long? = null,
    val name: String,
    val link: String
)