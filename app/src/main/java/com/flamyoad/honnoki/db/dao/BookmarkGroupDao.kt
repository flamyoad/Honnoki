package com.flamyoad.honnoki.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.flamyoad.honnoki.model.BookmarkGroup
import com.flamyoad.honnoki.model.BookmarkGroupWithCoverImages
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkGroupDao {
    @Transaction
    @Query("SELECT * FROM bookmark_group")
    fun getAllWithCoverImages(): LiveData<List<BookmarkGroupWithCoverImages>>

    @Query("SELECT * FROM bookmark_group")
    suspend fun getAll(): List<BookmarkGroup>

    @Insert
    suspend fun insert(bookmarkGroup: BookmarkGroup)

    @Query("""
        SELECT COUNT(*) >= 1 FROM bookmark 
        WHERE bookmarkGroupId = :bookmarkGroupId AND mangaOverviewId = :overviewId
""")
    suspend fun hasBookmarkedItems(bookmarkGroupId: Long, overviewId: Long): Boolean
}