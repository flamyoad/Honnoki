package com.flamyoad.honnoki.di

import com.flamyoad.honnoki.repository.*
import com.flamyoad.honnoki.repository.download.DownloadRepository
import com.flamyoad.honnoki.repository.system.BrightnessRepository
import com.flamyoad.honnoki.repository.system.BrightnessRepositoryImpl
import com.flamyoad.honnoki.repository.system.SystemInfoRepository
import com.flamyoad.honnoki.repository.system.SystemInfoRepositoryImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModules = module {
    single { BookmarkRepository(get()) }
    single { ChapterRepository(get()) }
    single { OverviewRepository(get()) }
    single { ReadHistoryRepository(get()) }
    single { DownloadRepository(get(), get(), androidApplication()) }
    single { PageRepository(get()) }
    single<SystemInfoRepository> { SystemInfoRepositoryImpl(androidApplication()) }
    single<BrightnessRepository> {
        BrightnessRepositoryImpl(
            get(),
            get(named(KoinConstants.APP_SCOPE))
        )
    }
}