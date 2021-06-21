package com.flamyoad.honnoki.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.flamyoad.honnoki.data.entities.BookmarkGroupWithInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkGroupWithInfoDao {
    @Transaction
    @Query(
        """
        SELECT bg.*
        FROM bookmark_group as bg
        LEFT OUTER JOIN bookmark ON bg.id = bookmark.bookmarkGroupId
        GROUP BY bg.id
    """
    )
    fun getAll(): Flow<List<BookmarkGroupWithInfo>>
}