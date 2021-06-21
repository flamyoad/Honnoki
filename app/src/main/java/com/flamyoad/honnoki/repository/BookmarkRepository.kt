package com.flamyoad.honnoki.repository

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
            bookmarkDao.updateBookmarkGroup(bookmarkIds, groupId)
        }
    }
}