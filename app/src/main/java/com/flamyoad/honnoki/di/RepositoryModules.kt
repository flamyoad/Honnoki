package com.flamyoad.honnoki.di

import com.flamyoad.honnoki.repository.BookmarkRepository
import org.koin.dsl.module

val repositoryModules = module {
    single { BookmarkRepository(get()) }
}