package com.flamyoad.honnoki.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class PageWithChapterInfo(
    @Embedded
    val page: Page,

    @Relation(parentColumn = "chapterId", entityColumn = "id")
    val chapter: Chapter
)