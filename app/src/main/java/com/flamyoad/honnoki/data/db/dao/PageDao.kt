package com.flamyoad.honnoki.data.db.dao

import androidx.room.*
import com.flamyoad.honnoki.data.model.Page
import com.flamyoad.honnoki.data.model.PageWithChapterInfo

@Dao
interface PageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(pageList: List<Page>)

    @Transaction
    @Query("SELECT * FROM page WHERE chapterId = :chapterId")
    fun getAllFromChapter(chapterId: Long): List<PageWithChapterInfo>
}