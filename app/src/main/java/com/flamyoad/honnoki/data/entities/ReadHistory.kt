package com.flamyoad.honnoki.data.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.flamyoad.honnoki.source.model.Source
import java.time.LocalDateTime

data class ReadHistory(
    @Embedded val overview: MangaOverview,

    @Relation(parentColumn = "lastReadChapterId", entityColumn = "id", entity = Chapter::class)
    val chapter: Chapter
)
