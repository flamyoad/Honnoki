package com.flamyoad.honnoki.di

import com.flamyoad.honnoki.api.BaseApi
import com.flamyoad.honnoki.api.MangaTownApi
import com.flamyoad.honnoki.api.MangakalotApi
import com.flamyoad.honnoki.api.SenMangaApi
import com.flamyoad.honnoki.parser.MangaTownParser
import com.flamyoad.honnoki.parser.MangakalotParser
import com.flamyoad.honnoki.parser.SenMangaParser
import org.koin.core.qualifier.named
import org.koin.dsl.module

val apiModules = module {
    factory { MangakalotApi(get(), get()) }
    factory { SenMangaApi(get(), get()) }
    factory { MangaTownApi(get(), get()) }

    single { MangakalotParser() }
    single { SenMangaParser() }
    single { MangaTownParser() }
}