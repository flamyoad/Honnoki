package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.model.Manga
import com.flamyoad.honnoki.model.SearchResult
import com.flamyoad.honnoki.ui.search.model.SearchGenre
import com.flamyoad.honnoki.utils.GenreConstants

abstract class BaseApi {
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