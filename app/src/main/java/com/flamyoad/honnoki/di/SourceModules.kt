package com.flamyoad.honnoki.di

import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.source.MangakalotSource
import com.flamyoad.honnoki.source.SenMangaSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

@ExperimentalPagingApi
val sourceModules = module {
    factory { MangakalotSource(get(), androidContext(), get()) }
    factory { SenMangaSource(get(), androidContext(), get()) }
}