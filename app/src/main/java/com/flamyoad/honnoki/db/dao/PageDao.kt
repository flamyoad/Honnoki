package com.flamyoad.honnoki.db.dao

import androidx.room.*
import com.flamyoad.honnoki.model.Page
import com.flamyoad.honnoki.model.PageWithChapterInfo

@Dao
interface PageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(pageList: List<Page>)

    @Transaction
    @Query("SELECT * FROM page WHERE chapterId = :chapterId")
    fun getAllFromChapter(chapterId: Long): List<PageWithChapterInfo>
}