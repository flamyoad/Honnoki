package com.flamyoad.honnoki.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.flamyoad.honnoki.model.BookmarkGroupWithInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkGroupWithInfoDao {
    @Transaction
    @Query(
        """
        SELECT bg.*, COUNT(bookmark.id) AS item_count
        FROM bookmark_group as bg
        LEFT OUTER JOIN bookmark ON bg.id = bookmark.bookmarkGroupId
        GROUP BY bg.id
    """
    )
    fun getAll(): Flow<List<BookmarkGroupWithInfo>>
}