package com.flamyoad.honnoki.repository

import android.content.Context
import androidx.room.withTransaction
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.entities.Chapter
import com.flamyoad.honnoki.data.entities.MangaOverview

class ChapterRepository(private val db: AppDatabase) {

    private val chapterDao get() = db.chapterDao()

    suspend fun markChapterAsRead(chapter: Chapter) {
        if (chapter.hasBeenRead) return

        db.withTransaction {
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

    suspend fun clearExistingChapters(overview: MangaOverview) {
        if (overview.id == null) return
        db.withTransaction {
            chapterDao.deleteAllFromOverview(overview.id)
        }
    }
}