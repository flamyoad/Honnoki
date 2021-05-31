package com.flamyoad.honnoki.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chapters",
    indices = [
        Index(value = ["mangaOverviewId"]),
        Index(value = ["link"], unique = true)
    ],
    foreignKeys = [androidx.room.ForeignKey(
        entity = MangaOverview::class,
        parentColumns = ["id"],
        childColumns = ["mangaOverviewId"],
        onDelete = androidx.room.ForeignKey.CASCADE,
        onUpdate = androidx.room.ForeignKey.CASCADE
    )]
)
data class Chapter(
    @PrimaryKey
    val id: Long? = null,

    val title: String,
    val date: String,
    val link: String,
    val mangaOverviewId: Long = -1
)