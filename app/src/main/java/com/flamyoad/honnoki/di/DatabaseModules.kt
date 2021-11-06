package com.flamyoad.honnoki.di

import android.content.Context
import androidx.room.Room
import com.flamyoad.honnoki.data.db.AppDatabase
import com.flamyoad.honnoki.data.db.DATABASE_NAME
import com.flamyoad.honnoki.data.db.DatabasePrePopulateCallback
import com.flamyoad.honnoki.data.db.migrations.MIGRATION_2_TO_3
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModules = module {
    single { provideDatabase(androidContext()) }
}

fun provideDatabase(context: Context): AppDatabase {
    return AppDatabase.provideDatabase(context)
}