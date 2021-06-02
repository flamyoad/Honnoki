package com.flamyoad.honnoki.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flamyoad.honnoki.model.Chapter
import com.flamyoad.honnoki.model.MangaOverview
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chapterList: List<Chapter>)

    @Query("SELECT * FROM chapters WHERE mangaOverviewId = :overviewId")
    fun getByOverviewId(overviewId: Long): Flow<List<Chapter>>

    @Query("SELECT * FROM chapters WHERE id = :id")
    fun get(id: Long): Chapter?

    @Query("SELECT link FROM chapters WHERE id = :id")
    fun getLink(id: Long): String

    @Query("""
        SELECT * FROM chapters
        WHERE mangaOverviewId = :overviewId AND id < :currentChapterId
        ORDER BY id DESC
        """)
    fun getPreviousChapter(overviewId: Long, currentChapterId: Long): Chapter?

    @Query("""
        SELECT * FROM chapters
        WHERE mangaOverviewId = :overviewId AND id > :currentChapterId
        ORDER BY id
        """)
    fun getNextChapter(overviewId: Long, currentChapterId: Long): Chapter?
}