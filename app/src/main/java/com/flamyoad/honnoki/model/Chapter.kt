package com.flamyoad.honnoki.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chapters",
    indices = [
        Index(value = ["mangaOverviewId"]),
        Index(value = ["link"], unique = true)
    ],
    foreignKeys = [ForeignKey(
        entity = MangaOverview::class,
        parentColumns = ["id"],
        childColumns = ["mangaOverviewId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Chapter(
    @PrimaryKey
    val id: Long? = null,

    val title: String,
    val date: String,
    val link: String,
    val mangaOverviewId: Long = -1
) {

    companion object {
        fun empty() = Chapter(
            id = -1,
            title = "",
            date = "",
            link = "",
            mangaOverviewId = -1
        )
    }
}