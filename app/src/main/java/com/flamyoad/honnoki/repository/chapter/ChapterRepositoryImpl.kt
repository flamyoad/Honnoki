package com.flamyoad.honnoki.repository.chapter

import androidx.room.withTransaction
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.entities.Chapter
import com.flamyoad.honnoki.data.entities.MangaOverview
import com.flamyoad.honnoki.source.BaseSource

class ChapterRepositoryImpl(private val db: AppDatabase): ChapterRepository {

    private val chapterDao get() = db.chapterDao()

    override suspend fun fetchChapterImages(chapter: Chapter, source: BaseSource) {
        val chapterId = chapter.id ?: return
        when (val result = source.getImages(chapter.link)) {
            is State.Success -> {
                val pages = result.value.map { it.copy(chapterId = chapterId) }
                db.withTransaction { db.pageDao().insertAll(pages) }
            }
        }
    }

    override suspend fun markChapterAsRead(chapter: Chapter) {
        if (chapter.hasBeenRead) return

        db.withTransaction {
            val readChapter = chapter.copy(hasBeenRead = true)
            chapterDao.update(readChapter)
        }
    }

    override suspend fun markChapterAsDownloaded(chapter: Chapter) {
        if (chapter.hasBeenDownloaded) return

        val readChapter = chapter.copy(hasBeenDownloaded = true)
        db.withTransaction {
            chapterDao.update(readChapter)
        }
    }

    override suspend fun clearExistingChapters(overview: MangaOverview) {
        if (overview.id == null) return
        db.withTransaction {
            chapterDao.deleteAllFromOverview(overview.id)
        }
    }
}