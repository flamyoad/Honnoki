package com.flamyoad.honnoki.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flamyoad.honnoki.data.model.MangaOverview
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface MangaOverviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(overview: MangaOverview): Long

    @Query("SELECT * FROM manga_overview WHERE id = :id")
    fun getById(id: Long): Flow<MangaOverview>

    @Query("SELECT * FROM manga_overview WHERE id = :id")
    suspend fun getByIdBlocking(id: Long): MangaOverview

    @Query("SELECT * FROM manga_overview WHERE link = :link")
    fun getByLink(link: String): LiveData<MangaOverview>

    @Query("SELECT * FROM manga_overview WHERE link = :link")
    fun getByLinkBlocking(link: String): MangaOverview?

    @Query("SELECT EXISTS(SELECT * FROM bookmark WHERE mangaOverviewId = :overviewId)")
    fun hasBeenBookmarked(overviewId: Long): Flow<Boolean>

    @Query("""
        UPDATE manga_overview 
        SET lastReadChapterId = :chapterId, lastReadTime = :readTime
        WHERE id = :overviewId 
       """)
    fun updateLastReadChapter(chapterId: Long, readTime: LocalDateTime, overviewId: Long): Int
}