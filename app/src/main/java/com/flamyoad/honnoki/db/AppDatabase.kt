package com.flamyoad.honnoki.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flamyoad.honnoki.db.dao.MangaDao
import com.flamyoad.honnoki.db.typeconverters.MangaTypeConverter
import com.flamyoad.honnoki.db.typeconverters.SourceConverter
import com.flamyoad.honnoki.model.Manga

const val DATABASE_NAME = "com.flamyoad.android.honnoki.AppDatabase"

@Database(entities = arrayOf(
    Manga::class
    ), version = 1)

@TypeConverters(SourceConverter::class, MangaTypeConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun mangaDao(): MangaDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance // return instance
            }
        }
    }
}