package com.flamyoad.honnoki.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "manga_overview",
    indices = [Index(value = ["link"], unique = true)]
)
data class MangaOverview(
    @PrimaryKey
    val id: Long? = null,

    val coverImage: String,
    val mainTitle: String,
    val alternativeTitle: String,
    val summary: String,
    val status: String,
    val source: Source?,
    val link: String
) {
    companion object {
        fun empty(): MangaOverview {
            return MangaOverview(
                coverImage = "",
                mainTitle = "",
                alternativeTitle = "",
                summary = "",
                status = "",
                source = null,
                link = ""
            )
        }
    }
}
