package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.model.Manga
import com.flamyoad.honnoki.model.SearchResult

abstract class BaseApi {
    abstract suspend fun searchForLatestManga(index: Int): List<Manga>

    abstract suspend fun searchForTrendingManga(index: Int): List<Manga>

    open suspend fun searchByKeyword(keyword: String, index: Int): List<SearchResult> {
        return emptyList()
    }
}