package com.flamyoad.honnoki.repository

import androidx.room.withTransaction
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.exception.NullEntityIdException
import com.flamyoad.honnoki.data.model.Bookmark
import com.flamyoad.honnoki.data.model.BookmarkGroup

class BookmarkRepository(private val db: AppDatabase) {

    val bookmarkDao get() = db.bookmarkDao()
    val groupDao get() = db.bookmarkGroupDao()

    suspend fun moveBookmarksToGroup(items: List<Bookmark>, group: BookmarkGroup) {
        db.withTransaction {
            val groupId = group.id ?: throw NullEntityIdException()
            val newItems = items.map {
                it.copy(bookmarkGroupId = groupId)
            }
            bookmarkDao.update(newItems)
        }
    }
}