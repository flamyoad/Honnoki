package com.flamyoad.honnoki.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flamyoad.honnoki.data.db.dao.*
import com.flamyoad.honnoki.data.db.typeconverters.LocalDateTimeConverter
import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.data.db.typeconverters.MangaTypeConverter
import com.flamyoad.honnoki.data.db.typeconverters.SourceConverter

const val DATABASE_NAME = "com.flamyoad.android.honnoki.AppDatabase"

@Database(
    entities = [
        Manga::class,
        MangaOverview::class,
        Chapter::class,
        Page::class,
        Author::class,
        Genre::class,
        SearchResult::class,
        Bookmark::class,
        BookmarkGroup::class],
    version = 1
)

@TypeConverters(SourceConverter::class, MangaTypeConverter::class, LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mangaDao(): MangaDao
    abstract fun mangaOverviewDao(): MangaOverviewDao
    abstract fun chapterDao(): ChapterDao
    abstract fun pageDao(): PageDao
    abstract fun searchResultDao(): SearchResultDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun bookmarkGroupDao(): BookmarkGroupDao
    abstract fun bookmarkGroupWithInfoDao(): BookmarkGroupWithInfoDao
    abstract fun genreDao(): GenreDao
    abstract fun authorDao(): AuthorDao
    abstract fun readHistoryDao(): ReadHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance // return instance
            }
        }
    }
}