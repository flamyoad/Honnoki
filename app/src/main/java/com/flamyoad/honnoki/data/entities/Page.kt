package com.flamyoad.honnoki.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "page",
    indices = [
        Index("chapterId"),
        Index("link", unique = true)
    ],
    foreignKeys = [ForeignKey(
        entity = Chapter::class,
        parentColumns = ["id"],
        childColumns = ["chapterId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Page(
    @PrimaryKey val id: Long? = null,
    val number: Int,
    val chapterId: Long? = null,
    val link: String,
    val linkDataSaver: String? = null,
)