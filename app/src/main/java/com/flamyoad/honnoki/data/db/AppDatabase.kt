package com.flamyoad.honnoki.data.db

import android.content.Context
import androidx.room.*
import com.flamyoad.honnoki.data.db.dao.*
import com.flamyoad.honnoki.data.db.migrations.MIGRATION_2_TO_3
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
        LookupResult::class,
        Bookmark::class,
        BookmarkGroup::class],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ]
)
@TypeConverters(
    SourceConverter::class,
    MangaTypeConverter::class,
    LocalDateTimeConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mangaDao(): MangaDao
    abstract fun mangaOverviewDao(): MangaOverviewDao
    abstract fun chapterDao(): ChapterDao
    abstract fun pageDao(): PageDao
    abstract fun searchResultDao(): SearchResultDao
    abstract fun lookupDao(): LookupDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun bookmarkGroupDao(): BookmarkGroupDao
    abstract fun bookmarkGroupWithInfoDao(): BookmarkGroupWithInfoDao
    abstract fun genreDao(): GenreDao
    abstract fun authorDao(): AuthorDao
    abstract fun readHistoryDao(): ReadHistoryDao

    companion object {
        fun provideDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .addCallback(DatabasePrePopulateCallback(context.resources))
                .addMigrations(MIGRATION_2_TO_3)
                .build()
        }
    }

}