package com.flamyoad.honnoki.di

import app.cash.quickjs.QuickJs
import com.flamyoad.honnoki.api.*
import com.flamyoad.honnoki.parser.*
import com.flamyoad.honnoki.parser.json.dm5.DM5Deobfuscator
import com.flamyoad.honnoki.parser.json.dm5.DM5JsonAdapter
import org.koin.core.qualifier.named
import org.koin.dsl.module

val apiModules = module {
    // Apis
    factory { MangakalotApi(get(), get()) }
    factory { SenMangaApi(get(), get()) }
    factory { MangaTownApi(get(), get()) }
    factory { ReadMangaApi(get(), get()) }
    factory { DM5Api(get(), get()) }

    // Parsers
    single { MangakalotParser() }
    single { SenMangaParser() }
    single { MangaTownParser() }
    single { ReadMangaParser() }
    single { DM5Parser(get(), get()) }

    // Moshi Adapter
    single { DM5JsonAdapter() }

    // Deobfuscator
    single { DM5Deobfuscator() }
}