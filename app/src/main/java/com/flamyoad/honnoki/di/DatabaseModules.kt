package com.flamyoad.honnoki.di

import android.content.Context
import androidx.room.Room
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.db.DATABASE_NAME
import org.koin.dsl.module

val dbModules = module {
    single { provideDatabase(get()) }
}

fun provideDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        DATABASE_NAME
    ).build()
}