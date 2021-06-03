package com.flamyoad.honnoki.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.flamyoad.honnoki.model.BookmarkGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkGroupDao {

    @Insert
    suspend fun insert(bookmarkGroup: BookmarkGroup)

    @Update
    suspend fun update(bookmarkGroup: BookmarkGroup)

    @Query("DELETE FROM bookmark_group WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT EXISTS(SELECT * FROM bookmark_group WHERE name = :name)")
    fun existsByName(name: String): Flow<Boolean>

    @Query("SELECT * FROM bookmark_group WHERE id = :id")
    fun getById(id: Long): Flow<BookmarkGroup?>

    @Query("SELECT id FROM bookmark_group LIMIT 1")
    suspend fun getFirstItemId(): Long

    @Query("SELECT * FROM bookmark_group")
    fun getAll(): Flow<List<BookmarkGroup>>

    @Query("SELECT * FROM bookmark_group")
    suspend fun getAllBlocking(): List<BookmarkGroup>

    @Query(
        """
        SELECT COUNT(*) >= 1 FROM bookmark 
        WHERE bookmarkGroupId = :bookmarkGroupId AND mangaOverviewId = :overviewId
"""
    )
    suspend fun hasBookmarkedItems(bookmarkGroupId: Long, overviewId: Long): Boolean

    @Transaction
    @Query("""
        SELECT manga_overview.coverImage FROM bookmark
        INNER JOIN manga_overview ON bookmark.mangaOverviewId = manga_overview.id
        WHERE bookmarkGroupId = :bookmarkGroupId
    """)
    suspend fun getCoverImagesFrom(bookmarkGroupId: Long): List<String>
}