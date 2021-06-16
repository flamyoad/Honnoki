package com.flamyoad.honnoki.parser.json.dm5

import com.squareup.moshi.Moshi

class DM5JsonAdapter {
    private val moshi by lazy { Moshi.Builder().build() }

    val recentMangaConverter by lazy { moshi.adapter(DM5RecentMangaJson::class.java) }
}