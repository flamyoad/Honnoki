package com.flamyoad.honnoki.data.model

import androidx.room.*

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
    val link: String,
    val title: String,
    val number: Double,
    val date: String,
    val hasBeenRead: Boolean,
    val hasBeenDownloaded: Boolean,
    val mangaOverviewId: Long = -1
) {

    companion object {
        fun empty() = Chapter(
            id = -1,
            title = "",
            number = 0.00,
            date = "",
            link = "",
            hasBeenRead = false,
            hasBeenDownloaded = false,
            mangaOverviewId = -1
        )
    }
}