package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.api.handler.ApiRequestHandler
import com.flamyoad.honnoki.api.handler.NetworkResult
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.network.MangaTownService
import com.flamyoad.honnoki.parser.MangaTownParser
import com.flamyoad.honnoki.utils.extensions.stringSuspending
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Refactor to use ApiRequestHandler if this source is enabled. Currently is disabled
class MangaTownApi(
    private val service: MangaTownService,
    private val parser: MangaTownParser,
    apiHandler: ApiRequestHandler
) : BaseApi(apiHandler) {

    override val startingPageIndex: Int
        get() = 1

    override suspend fun searchForLatestManga(index: Int): State<List<Manga>> {
        return processApiData(
            apiCall = { service.getLatestManga(index) },
            parseData = { parser.parseForRecentMangas(it.stringSuspending()) }
        )
    }

    override suspend fun searchForTrendingManga(index: Int): State<List<Manga>> {
        return processApiData(
            apiCall = { service.getTrendingManga(index) },
            parseData = { parser.parseForTrendingMangas(it.stringSuspending()) }
        )
    }

    suspend fun searchForMangaOverview(link: String): State<MangaOverview> {
        return processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseForMangaOverview(it.stringSuspending(), link) }
        )
    }

    suspend fun searchForGenres(link: String): State<List<Genre>> {
        return processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseForGenres(it.stringSuspending()) }
        )
    }

    suspend fun searchForAuthors(link: String): State<List<Author>> {
        return processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseForAuthors(it.stringSuspending()) }
        )
    }

    suspend fun searchForChapterList(link: String): State<List<Chapter>> {
        return processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseForChapterList(it.stringSuspending()) }
        )
    }

    // Gets the list of pages from the right hand dropdown-box
    suspend fun searchForPageList(link: String): List<String> {
        val result = processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseForPageList(it.stringSuspending()) }
        )
        if (result is State.Success) {
            return result.value
        } else {
            return emptyList()
        }
    }

    suspend fun getImageFromPage(link: String, index: Int): State<Page> {
        return processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseImageFromPage(it.stringSuspending(), index) }
        )
    }

}