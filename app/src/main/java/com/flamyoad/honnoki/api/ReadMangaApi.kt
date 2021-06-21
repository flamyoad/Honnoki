package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.network.ReadMangaService
import com.flamyoad.honnoki.parser.ReadMangaParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ReadMangaApi(
    private val service: ReadMangaService,
    private val parser: ReadMangaParser
) : BaseApi() {

    override val startingPageIndex: Int
        get() = 1

    override suspend fun searchForLatestManga(index: Int): List<Manga> {
        println("search for latest")
        val response = service.getLatestManga(index)

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val mangaList = parser.parseForRecentMangas(html)

            return@withContext mangaList
        }
    }

    override suspend fun searchForTrendingManga(index: Int): List<Manga> {
        val response = service.getTrendingManga(index)

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val mangaList = parser.parseForTrendingMangas(html)

            return@withContext mangaList
        }
    }

    suspend fun searchForMangaOverview(link: String): State<MangaOverview> {
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

    suspend fun searchForGenres(link: String): State<List<Genre>> {
        val response = try {
            service.getHtml(link)
        } catch (e: Exception) {
            return State.Error(e)
        }

        return withContext(Dispatchers.Default) {
            val html = response.string()

            val genres = parser.parseForGenres(html)
            return@withContext State.Success(genres)
        }
    }

    suspend fun searchForAuthors(link: String): State<List<Author>> {
        val response = try {
            service.getHtml(link)
        } catch (e: Exception) {
            return State.Error(e)
        }

        return withContext(Dispatchers.Default) {
            val html = response.string()

            val genres = parser.parseForAuthors(html)
            return@withContext State.Success(genres)
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

        val html = response.string()

        return withContext(Dispatchers.Default) {
            val imageList = parser.parseForPageList(html)
            return@withContext State.Success(imageList)
        }
    }

    override suspend fun searchByKeyword(keyword: String, index: Int): List<SearchResult> {
        val response = service.searchByKeyword(keyword)

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val searchResultList = parser.parseForSearchByKeyword(html)

            return@withContext searchResultList
        }
    }
}