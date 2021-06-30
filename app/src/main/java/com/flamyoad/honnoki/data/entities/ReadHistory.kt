package com.flamyoad.honnoki.data.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.flamyoad.honnoki.source.model.Source
import java.time.LocalDateTime

data class ReadHistory(
//    val overviewId: Long,
//    val coverImage: String,
//    val mainTitle: String,
//    val overviewLink: String,
//    val lastReadChapterId: Long,
//    val lastReadDateTime: LocalDateTime,
//    val lastReadPageNumber: Int,
//    val source: Source,
    @Embedded val overview: MangaOverview,

    @Relation(parentColumn = "lastReadChapterId", entityColumn = "id", entity = Chapter::class)
    val chapter: Chapter
)
