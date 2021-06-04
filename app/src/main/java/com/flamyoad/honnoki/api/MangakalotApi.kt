package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.model.*
import com.flamyoad.honnoki.network.MangakalotService
import com.flamyoad.honnoki.parser.MangakalotParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MangakalotApi(private val service: MangakalotService): BaseApi() {
    private val parser = MangakalotParser()

    override suspend fun searchForLatestManga(index: Int): List<Manga> {
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
            val mangaList = parser.parseForTrendingManga(html)

            return@withContext mangaList
        }
    }

    override suspend fun searchForTopManga(index: Int): List<Manga> {
        val response = service.getTopWeekManga()

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val mangaList = parser.parseForTopManga(html)

            return@withContext mangaList
        }
    }

    override suspend fun searchForNewManga(index: Int): List<Manga> {
        val response = service.getNewManga(index)

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val mangaList = parser.parseForNewManga(html)

            return@withContext mangaList
        }
    }

    suspend fun searchForMangaOverview(link: String): State<MangaOverview> {
        val response = try {
            service.getMangaOverview(link)
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
            service.getGenres(link)
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
            service.getAuthors(link)
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
            service.getMangaOverview(link)
        } catch (e: Exception) {
            return State.Error(e)
        }

        return withContext(Dispatchers.Default) {
            val html = response.string()

            val mangaOverview = parser.parseForChapterList(html)
            return@withContext State.Success(mangaOverview)
        }
    }

    suspend fun searchForImageList(link: String): State<List<Page>> {
        val response = try {
            service.getMangaOverview(link)
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