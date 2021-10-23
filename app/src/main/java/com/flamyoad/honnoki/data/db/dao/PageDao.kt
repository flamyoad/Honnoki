package com.flamyoad.honnoki.data.db.dao

import androidx.room.*
import com.flamyoad.honnoki.data.entities.Page
import com.flamyoad.honnoki.data.entities.PageWithChapterInfo

@Dao
interface PageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(pageList: List<Page>)

    @Query("SELECT * FROM page WHERE chapterId = :chapterId")
    suspend fun getPages(chapterId: Long): List<Page>

    @Transaction
    @Query("SELECT * FROM page WHERE chapterId = :chapterId")
    fun getPagesWithChapterInfo(chapterId: Long): List<PageWithChapterInfo>
}