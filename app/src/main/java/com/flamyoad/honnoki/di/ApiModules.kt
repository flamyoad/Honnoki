package com.flamyoad.honnoki.di

import com.flamyoad.honnoki.api.*
import com.flamyoad.honnoki.api.handler.ApiRequestHandler
import com.flamyoad.honnoki.parser.*
import com.flamyoad.honnoki.parser.json.dm5.DM5Deobfuscator
import com.flamyoad.honnoki.parser.json.dm5.DM5JsonAdapter
import com.flamyoad.honnoki.parser.json.senmanga.SenmangaJsonAdapter
import org.koin.dsl.module

val apiModules = module {
    // Api handler
    single { ApiRequestHandler() }

    // Apis
    factory { MangakalotApi(get(), get(), get()) }
    factory { SenMangaApi(get(), get(), get()) }
    factory { MangaTownApi(get(), get(), get()) }
    factory { ReadMangaApi(get(), get(), get()) }
    factory { DM5Api(get(), get(), get()) }
    factory { MangadexApi(get(), get(), get()) }

    // Parsers
    single { MangakalotParser() }
    single { SenMangaParser(get()) }
    single { MangaTownParser() }
    single { ReadMangaParser() }
    single { DM5Parser(get(), get()) }
    single { MangadexParser() }

    // Moshi Adapter
    single { DM5JsonAdapter() }
    single { SenmangaJsonAdapter() }

    // Deobfuscator
    single { DM5Deobfuscator() }
}