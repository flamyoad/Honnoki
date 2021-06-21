package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.data.entities.Manga
import com.flamyoad.honnoki.data.entities.SearchResult
import com.flamyoad.honnoki.data.GenreConstants

abstract class BaseApi {
    abstract val startingPageIndex: Int

    open suspend fun searchForLatestManga(index: Int): List<Manga> {
        return emptyList()
    }

    open suspend fun searchForTrendingManga(index: Int): List<Manga> {
        return emptyList()
    }

    open suspend fun searchForTopManga(index: Int): List<Manga> {
        return emptyList()
    }

    open suspend fun searchForNewManga(index: Int): List<Manga> {
        return emptyList()
    }

    open suspend fun searchByKeyword(keyword: String, index: Int): List<SearchResult> {
        return emptyList()
    }

    open suspend fun searchByKeywordAndGenres(
        keyword: String,
        genre: GenreConstants,
        index: Int
    ): List<SearchResult> {
        return emptyList()
    }
}