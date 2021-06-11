package com.flamyoad.honnoki.data.model

import androidx.room.Embedded
import androidx.room.Relation
import java.time.LocalDateTime

data class ReadHistory(
    val overviewId: Long,
    val coverImage: String,
    val mainTitle: String,
    val overviewLink: String,
    val lastReadChapterId: Long,
    val lastReadTime: LocalDateTime,
    val lastReadPageNumber: Int,
    val source: Source,

    @Relation(parentColumn = "overviewId", entityColumn = "id")
    val chapter: Chapter
)
