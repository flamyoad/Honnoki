package com.flamyoad.honnoki.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flamyoad.honnoki.data.model.Chapter
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(chapterList: List<Chapter>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllForceRefresh(chapterList: List<Chapter>)

    @Query("SELECT * FROM chapters WHERE mangaOverviewId = :overviewId ORDER BY number ")
    fun getAscByOverviewId(overviewId: Long): Flow<List<Chapter>>

    @Query("SELECT * FROM chapters WHERE mangaOverviewId = :overviewId ORDER BY number DESC")
    fun getDescByOverviewId(overviewId: Long): Flow<List<Chapter>>

    @Query("SELECT * FROM chapters WHERE id = :id")
    fun get(id: Long): Chapter?

    @Query("SELECT link FROM chapters WHERE id = :id")
    fun getLink(id: Long): String

    @Query("SELECT COUNT(id) FROM page WHERE chapterId = :chapterId")
    fun getTotalPages(chapterId: Long): Flow<Int>

    @Query("""
        SELECT * FROM chapters
        WHERE mangaOverviewId = :overviewId AND number < :currentChapterNumber
        ORDER BY number DESC
        """)
    fun getPreviousChapter(overviewId: Long, currentChapterNumber: Double): Chapter?

    @Query("""
        SELECT * FROM chapters
        WHERE mangaOverviewId = :overviewId AND number > :currentChapterNumber
        ORDER BY number
        """)
    fun getNextChapter(overviewId: Long, currentChapterNumber: Double): Chapter?
}