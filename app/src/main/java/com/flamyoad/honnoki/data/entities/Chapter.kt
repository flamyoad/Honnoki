package com.flamyoad.honnoki.data.entities

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

    @ColumnInfo(defaultValue = "") // Needed for auto migration
    val translatedLanguage: String,

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
            translatedLanguage = "",
            mangaOverviewId = -1
        )
    }
}