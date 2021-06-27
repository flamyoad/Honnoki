package com.flamyoad.honnoki

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.db.dao.MangaDao
import com.flamyoad.honnoki.data.entities.Manga
import com.flamyoad.honnoki.data.entities.MangaType
import com.flamyoad.honnoki.source.model.Source
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class RoomUniqueConstraintTest {
    private lateinit var db: AppDatabase
    private lateinit var mangaDao: MangaDao

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        mangaDao = db.mangaDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun shouldIgnore_WhenInsertingDuplicates() {
        val listOfDuplicates = MutableList(3) {
            Manga(title = "One Piece", source = Source.MANGAKALOT, type = MangaType.TRENDING)
        }
        mangaDao.insertAll(listOfDuplicates)
        Assert.assertSame(mangaDao.getAll().size, 1)
    }

    fun shouldAllow_WhenInsertingNonDuplicates() {
        val listOfUniques = mutableListOf(
            Manga(title = "One Piece", source = Source.MANGAKALOT, type = MangaType.TRENDING),
            Manga(title = "One Piece", source = Source.MANGAKALOT, type = MangaType.RECENTLY),
            Manga(title = "One Piece", source = Source.SENMANGA, type = MangaType.TRENDING),
            Manga(title = "One Piece", source = Source.SENMANGA, type = MangaType.RECENTLY)
        )
        mangaDao.insertAll(listOfUniques)
        Assert.assertSame(mangaDao.getAll().size, 4)
    }
}