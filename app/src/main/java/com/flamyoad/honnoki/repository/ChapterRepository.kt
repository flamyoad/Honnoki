package com.flamyoad.honnoki.repository

import androidx.room.withTransaction
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.exception.NullEntityIdException
import com.flamyoad.honnoki.data.model.Chapter
import java.time.LocalDateTime

class ChapterRepository(private val db: AppDatabase) {

    private val chapterDao get() = db.chapterDao()
    private val overviewDao get() = db.mangaOverviewDao()

    suspend fun markChapterAsRead(chapter: Chapter, overviewId: Long) {
        db.withTransaction {
            val chapterId = chapter.id ?: throw NullEntityIdException()
            overviewDao.updateLastReadChapter(chapterId, LocalDateTime.now(), overviewId)

            if (chapter.hasBeenRead) return@withTransaction

            val readChapter = chapter.copy(hasBeenRead = true)
            chapterDao.update(readChapter)
        }
    }

    suspend fun markChapterAsDownloaded(chapter: Chapter) {
        if (chapter.hasBeenDownloaded) return

        val readChapter = chapter.copy(hasBeenDownloaded = true)
        db.withTransaction {
            chapterDao.update(readChapter)
        }
    }
}