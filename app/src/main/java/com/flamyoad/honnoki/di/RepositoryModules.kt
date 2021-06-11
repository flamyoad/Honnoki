package com.flamyoad.honnoki.di

import com.flamyoad.honnoki.repository.BookmarkRepository
import com.flamyoad.honnoki.repository.ChapterRepository
import com.flamyoad.honnoki.repository.OverviewRepository
import com.flamyoad.honnoki.repository.ReadHistoryRepository
import org.koin.dsl.module

val repositoryModules = module {
    single { BookmarkRepository(get()) }
    single { ChapterRepository(get()) }
    single { OverviewRepository(get()) }
    single { ReadHistoryRepository(get()) }
}