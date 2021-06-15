package com.flamyoad.honnoki.di

import com.flamyoad.honnoki.api.*
import com.flamyoad.honnoki.parser.MangaTownParser
import com.flamyoad.honnoki.parser.MangakalotParser
import com.flamyoad.honnoki.parser.ReadMangaParser
import com.flamyoad.honnoki.parser.SenMangaParser
import org.koin.core.qualifier.named
import org.koin.dsl.module

val apiModules = module {
    factory { MangakalotApi(get(), get()) }
    factory { SenMangaApi(get(), get()) }
    factory { MangaTownApi(get(), get()) }
    factory { ReadMangaApi(get(), get()) }

    single { MangakalotParser() }
    single { SenMangaParser() }
    single { MangaTownParser() }
    single { ReadMangaParser() }
}