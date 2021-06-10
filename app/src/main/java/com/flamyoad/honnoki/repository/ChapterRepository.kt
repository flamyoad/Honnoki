package com.flamyoad.honnoki.repository

import androidx.room.withTransaction
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.model.Chapter

class ChapterRepository(private val db: AppDatabase) {

    private val chapterDao get() = db.chapterDao()
    private val overviewDao get() = db.mangaOverviewDao()

    suspend fun markChapterAsRead(chapter: Chapter, overviewId: Long) {
        db.withTransaction {
            chapter.id?.let {
                db.mangaOverviewDao().updateLastReadChapter(it, overviewId)
            }

            if (chapter.hasBeenRead) return@withTransaction

            val readChapter = chapter.copy(hasBeenRead = true)
            db.chapterDao().update(readChapter)
        }
    }

    suspend fun markChapterAsDownloaded(chapter: Chapter) {
        if (chapter.hasBeenDownloaded) return

        val readChapter = chapter.copy(hasBeenDownloaded = true)
        db.withTransaction {
            db.chapterDao().update(readChapter)
        }
    }
}