package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.network.SenMangaService
import com.flamyoad.honnoki.parser.SenMangaParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SenMangaApi(
    private val service: SenMangaService,
    private val parser: SenMangaParser
) : BaseApi() {

    override val startingPageIndex: Int
        get() = 1

    override suspend fun searchForLatestManga(index: Int): List<Manga> {
        val response = service.getLatestManga(index)

        return withContext(Dispatchers.Default) {
            val url = response.string()
            val mangaList = parser.parseForRecentMangas(url)

            return@withContext mangaList
        }
    }

    override suspend fun searchForTrendingManga(index: Int): List<Manga> {
        val response = service.getLatestManga(index)

        return withContext(Dispatchers.Default) {
            val url = response.string()
            val mangaList = parser.parseForTrendingMangas(url)

            return@withContext mangaList
        }
    }

    suspend fun searchForOverview(link: String): State<MangaOverview> {
        val response = try {
            service.getHtml(link)
        } catch (e: Exception) {
            return State.Error(e)
        }

        return withContext(Dispatchers.Default) {
            val html = response.string()

            val mangaOverview = parser.parseForMangaOverview(html, link)
            return@withContext State.Success(mangaOverview)
        }
    }

    suspend fun searchForChapterList(link: String): State<List<Chapter>> {
        val response = try {
            service.getHtml(link)
        } catch (e: Exception) {
            return State.Error(e)
        }

        return withContext(Dispatchers.Default) {
            val html = response.string()

            val chapterList = parser.parseForChapterList(html)
            return@withContext State.Success(chapterList)
        }
    }

    suspend fun searchForImageList(link: String): State<List<Page>> {
        val response = try {
            service.getHtml(link)
        } catch (e: Exception) {
            return State.Error(e)
        }

        return withContext(Dispatchers.Default) {
            val html = response.string()

            val imageList = parser.parseForImageList(html)
            return@withContext State.Success(imageList)
        }
    }

    override suspend fun searchByKeyword(keyword: String, index: Int): List<SearchResult> {
        val response = service.searchByKeyword(keyword, index)

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val searchResultList = parser.parseForSearchByKeyword(html)

            return@withContext searchResultList
        }
    }
}