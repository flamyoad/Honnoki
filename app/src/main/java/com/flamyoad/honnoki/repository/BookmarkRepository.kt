package com.flamyoad.honnoki.repository

import android.database.sqlite.SQLiteConstraintException
import androidx.room.withTransaction
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.exception.NullEntityIdException
import com.flamyoad.honnoki.data.entities.BookmarkGroup

class BookmarkRepository(private val db: AppDatabase) {

    val bookmarkDao get() = db.bookmarkDao()
    val groupDao get() = db.bookmarkGroupDao()

    suspend fun moveBookmarksToGroup(bookmarkIds: List<Long>, group: BookmarkGroup) {
        db.withTransaction {
            val groupId = group.id ?: throw NullEntityIdException()
            val bookmarks = bookmarkDao.getAllFrom(bookmarkIds)
            for (bookmark in bookmarks) {
                val alreadyExists = bookmarkDao.alreadyExistsInGroup(groupId, bookmark.mangaOverviewId)
                if (alreadyExists) {
                    continue
                }
                bookmarkDao.updateBookmarkGroup(bookmark.id!!, groupId)
            }
        }
    }
}