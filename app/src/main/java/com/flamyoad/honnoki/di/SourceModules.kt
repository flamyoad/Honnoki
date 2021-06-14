package com.flamyoad.honnoki.di

import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.source.BaseSource
import com.flamyoad.honnoki.source.MangaTownSource
import com.flamyoad.honnoki.source.MangakalotSource
import com.flamyoad.honnoki.source.SenMangaSource
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

@ExperimentalPagingApi
val sourceModules = module {
    factory<BaseSource>(named(KoinConstants.MANGAKALOT)) { MangakalotSource(get(), androidContext(), get()) }
    factory<BaseSource>(named(KoinConstants.SENMANGA)) { SenMangaSource(get(), androidContext(), get()) }
    factory<BaseSource>(named(KoinConstants.MANGATOWN)) { MangaTownSource(get(), androidContext(), get()) }
}