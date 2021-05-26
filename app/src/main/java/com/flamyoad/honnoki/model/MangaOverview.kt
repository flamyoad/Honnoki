package com.flamyoad.honnoki.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manga_overview")
data class MangaOverview(
    @PrimaryKey val id: Long? = null,
    val coverImage: String,
    val mainTitle: String,
    val alternativeTitle: String,
    val summary: String,
    val authors: List<Author>,
    val status: String,
    val genres: List<Genre>,
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
                authors = emptyList(),
                status = "",
                genres = emptyList(),
                source = null,
                link = ""
            )
        }
    }
}
