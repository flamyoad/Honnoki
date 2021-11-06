package com.flamyoad.honnoki.data.entities

import androidx.annotation.NonNull
import androidx.room.*

@Entity(
    tableName = "page",
    indices = [
//        Index("chapterId"),
//        Index("link", unique = true)
        Index(value = ["chapterId", "number"], unique = true)
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