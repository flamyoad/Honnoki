package com.flamyoad.honnoki.repository

import androidx.room.withTransaction
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.exception.NullEntityIdException
import com.flamyoad.honnoki.data.entities.Chapter
import com.flamyoad.honnoki.data.entities.MangaOverview
import java.time.LocalDateTime

class OverviewRepository(private val db: AppDatabase) {

    private val overviewDao get() = db.mangaOverviewDao()

    suspend fun updateLastReadChapter(chapter: Chapter, overviewId: Long) {
        val chapterId = chapter.id ?: throw NullEntityIdException()
        db.withTransaction {
            overviewDao.updateLastReadChapter(chapterId, LocalDateTime.now(), overviewId)
        }
    }

    suspend fun updateLastReadPage(pageNumber: Int, overviewId: Long) {
        db.withTransaction {
            overviewDao.updateLastReadPage(pageNumber, overviewId)
        }
    }

    suspend fun delete(overview: MangaOverview) {
        if (overview.id == null) return
        db.withTransaction {
            overviewDao.delete(overview.id)
        }
    }
}