package com.flamyoad.honnoki.di

import com.flamyoad.honnoki.repository.BookmarkRepository
import com.flamyoad.honnoki.repository.ChapterRepository
import org.koin.dsl.module

val repositoryModules = module {
    single { BookmarkRepository(get()) }
    single { ChapterRepository(get()) }
}