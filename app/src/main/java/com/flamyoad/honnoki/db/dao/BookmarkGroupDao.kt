package com.flamyoad.honnoki.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.flamyoad.honnoki.model.BookmarkGroup
import com.flamyoad.honnoki.model.BookmarkGroupWithCoverImages
import com.flamyoad.honnoki.model.BookmarkWithOverview
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkGroupDao {

    @Insert
    suspend fun insert(bookmarkGroup: BookmarkGroup)

    @Query("SELECT EXISTS(SELECT * FROM bookmark_group WHERE name = :name)")
    fun existsByName(name: String): Flow<Boolean>

    @Query("SELECT * FROM bookmark_group LIMIT 1")
    suspend fun getFirst(): BookmarkGroup

    @Transaction
    @Query("SELECT * FROM bookmark_group")
    fun getAllWithCoverImages(): LiveData<List<BookmarkGroupWithCoverImages>>

    @Query("SELECT * FROM bookmark_group")
    suspend fun getAllBlocking(): List<BookmarkGroup>

    @Query("""
        SELECT COUNT(*) >= 1 FROM bookmark 
        WHERE bookmarkGroupId = :bookmarkGroupId AND mangaOverviewId = :overviewId
""")
    suspend fun hasBookmarkedItems(bookmarkGroupId: Long, overviewId: Long): Boolean
}