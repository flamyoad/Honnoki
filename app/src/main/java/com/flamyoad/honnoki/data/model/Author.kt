package com.flamyoad.honnoki.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "authors",
    indices = [Index(value = ["mangaOverviewId"])],
    foreignKeys = [ForeignKey(
        entity = MangaOverview::class,
        parentColumns = ["id"],
        childColumns = ["mangaOverviewId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Author(
    @PrimaryKey
    val id: Long? = null,

    val name: String,
    val link: String,
    val mangaOverviewId: Long = -1
)