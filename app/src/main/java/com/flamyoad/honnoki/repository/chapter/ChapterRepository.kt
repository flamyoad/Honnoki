package com.flamyoad.honnoki.repository.chapter

import com.flamyoad.honnoki.data.entities.Chapter
import com.flamyoad.honnoki.data.entities.MangaOverview
import com.flamyoad.honnoki.source.BaseSource

interface ChapterRepository {
    suspend fun fetchChapterImages(chapter: Chapter, source: BaseSource)
    suspend fun markChapterAsRead(chapter: Chapter)
    suspend fun markChapterAsDownloaded(chapter: Chapter)
    suspend fun clearExistingChapters(overview: MangaOverview)
}