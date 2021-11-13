package com.flamyoad.honnoki.di

import com.flamyoad.honnoki.repository.*
import com.flamyoad.honnoki.repository.download.DownloadRepository
import com.flamyoad.honnoki.repository.system.SystemInfoRepository
import com.flamyoad.honnoki.repository.system.SystemInfoRepositoryImpl
import com.flamyoad.honnoki.repository.theme.ThemeRepository
import com.flamyoad.honnoki.repository.theme.ThemeRepositoryImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val repositoryModules = module {
    single { BookmarkRepository(get()) }
    single { ChapterRepository(get()) }
    single { OverviewRepository(get()) }
    single { ReadHistoryRepository(get()) }
    single { DownloadRepository(get(), get(), androidApplication()) }
    single<SystemInfoRepository> { SystemInfoRepositoryImpl(androidApplication()) }
    single<ThemeRepository> { ThemeRepositoryImpl(get()) }
}