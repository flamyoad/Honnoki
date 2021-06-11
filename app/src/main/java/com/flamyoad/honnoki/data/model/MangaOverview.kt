package com.flamyoad.honnoki.data.model

import androidx.room.*
import java.time.LocalDateTime

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
    val link: String,
    val lastReadChapterId: Long,
    val lastReadTime: LocalDateTime,
    val lastReadPageNumber: Int
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
                link = "",
                lastReadChapterId = -1,
                lastReadTime = LocalDateTime.MIN,
                lastReadPageNumber = -1
            )
        }
    }
}
