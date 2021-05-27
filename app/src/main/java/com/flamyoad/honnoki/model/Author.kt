package com.flamyoad.honnoki.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "authors",
    indices = [androidx.room.Index(value = ["mangaOverviewId"])],
    foreignKeys = [androidx.room.ForeignKey(
        entity = MangaOverview::class,
        parentColumns = ["id"],
        childColumns = ["mangaOverviewId"],
        onDelete = androidx.room.ForeignKey.CASCADE,
        onUpdate = androidx.room.ForeignKey.CASCADE
    )]
)
data class Author(
    @PrimaryKey val id: Long? = null,
    val name: String,
    val link: String,
    val mangaOverviewId: Long = -1
)