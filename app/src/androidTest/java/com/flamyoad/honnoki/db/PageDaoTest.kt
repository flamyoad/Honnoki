package com.flamyoad.honnoki.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnit4
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.db.dao.ChapterDao
import com.flamyoad.honnoki.data.db.dao.PageDao
import com.flamyoad.honnoki.data.entities.Chapter
import com.flamyoad.honnoki.data.entities.MangaOverview
import com.flamyoad.honnoki.data.entities.Page
import com.flamyoad.honnoki.source.model.Source
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PageDaoTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var chapterDao: ChapterDao
    private lateinit var pageDao: PageDao

    private val overview =
        MangaOverview.empty().copy(id = 1, source = Source.MANGADEX)

    @Before
    fun setup() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .build()
        chapterDao = db.chapterDao()
        pageDao = db.pageDao()

        runBlocking {
            db.mangaOverviewDao().insert(overview)
        }
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        db.close()
    }

    @Test
    fun existingIdShouldNotChange_whenInsertingDuplicates() = runBlocking {
        val chapterId: Long = 123
        val chapter = Chapter.empty().copy(id = chapterId, mangaOverviewId = 1)
        val page =
            Page(
                1,
                1,
                chapterId,
                "https://img1.png",
                "https://img1-datasaver.png"
            )

        chapterDao.insertAll(listOf(chapter))
        repeat(3) {
            pageDao.insertAll(
                listOf(
                    page.copy(
                        link = generatePlaceholderImageLink(
                            it,
                            dataSaver = false
                        ),
                        linkDataSaver = generatePlaceholderImageLink(
                            it,
                            dataSaver = true
                        )
                    )
                )
            )
        }
        val insertedPages = pageDao.getPages(chapterId)
        Assert.assertEquals(insertedPages.size, 1)

        val firstPageInserted = insertedPages.first()
        Assert.assertEquals(firstPageInserted.id, 1L)
        Assert.assertEquals(
            firstPageInserted.link,
            generatePlaceholderImageLink(2)
        )
        Assert.assertEquals(
            firstPageInserted.linkDataSaver,
            generatePlaceholderImageLink(2, dataSaver = true)
        )


        // java.lang.AssertionError: expected: java.lang.Long<1> but was: java.lang.Integer<1>
        //	at org.junit.Assert.fail(Assert.java:88) OMG OMG OMG OMG
    }

    private fun generatePlaceholderImageLink(
        number: Int,
        dataSaver: Boolean = false
    ): String {
        return if (dataSaver) {
            "https://img${number}.png"
        } else {
            "https://img${number}-datasaver.png"
        }
    }

}