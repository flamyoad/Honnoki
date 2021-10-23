package com.flamyoad.honnoki.di

import com.flamyoad.honnoki.repository.*
import com.flamyoad.honnoki.repository.download.DownloadRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val repositoryModules = module {
    single { BookmarkRepository(get()) }
    single { ChapterRepository(get()) }
    single { OverviewRepository(get()) }
    single { ReadHistoryRepository(get()) }
    single { DownloadRepository(get(), get(), androidApplication()) }
}