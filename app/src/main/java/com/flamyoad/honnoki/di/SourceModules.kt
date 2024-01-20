package com.flamyoad.honnoki.di

import com.flamyoad.honnoki.source.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val sourceModules = module {
    factory<BaseSource>(named(KoinConstants.MANGAKALOT)) { MangakalotSource(get(), androidContext(), get()) }
    factory<BaseSource>(named(KoinConstants.SENMANGA)) { SenMangaSource(get(), androidContext(), get()) }
    factory<BaseSource>(named(KoinConstants.MANGATOWN)) { MangaTownSource(get(), androidContext(), get()) }
    factory<BaseSource>(named(KoinConstants.READMANGA)) { ReadMangaSource(get(), androidContext(), get()) }
    factory<BaseSource>(named(KoinConstants.DM5)) { DM5Source(get(), androidContext(), get()) }
    factory<BaseSource>(named(KoinConstants.MANGADEX)) { MangadexSource(get(), androidContext(), get()) }
}