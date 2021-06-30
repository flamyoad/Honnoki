package com.flamyoad.honnoki.di

import com.flamyoad.honnoki.cache.CacheManager
import com.flamyoad.honnoki.cache.CoverCache
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val cacheModules = module {
    single { CacheManager(get(), androidContext()) }
    single { CoverCache(androidContext()) }
}

