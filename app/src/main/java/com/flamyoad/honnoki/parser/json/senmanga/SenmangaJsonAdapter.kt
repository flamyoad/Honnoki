package com.flamyoad.honnoki.parser.json.senmanga

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class SenmangaJsonAdapter {
    private val moshi by lazy { Moshi.Builder().build() }

    val imageConverter: JsonAdapter<List<SenmangaImageJson>> by lazy {
        val listOfMangaTypes =
            Types.newParameterizedType(List::class.java, SenmangaImageJson::class.java)
        moshi.adapter(listOfMangaTypes)
    }
}