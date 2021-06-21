package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.network.MangaTownService
import com.flamyoad.honnoki.parser.MangaTownParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MangaTownApi(
    private val service: MangaTownService,
    private val parser: MangaTownParser
) : BaseApi() {

    override val startingPageIndex: Int
        get() = 1

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

    // Gets the list of pages from the right hand dropdown-box
    suspend fun searchForPageList(link: String): List<String> {
        val response = service.getHtml(link)
        val html = response.string()

        return withContext(Dispatchers.Default) {
            val pageList = parser.parseForPageList(html)
            return@withContext pageList
        }
    }

    suspend fun getImageFromPage(link: String, index: Int): State<Page> {
        val response = try {
            service.getHtml(link)
        } catch (e: Exception) {
            return State.Error(e)
        }

        val html = response.string()

        return withContext(Dispatchers.Default) {
            val image = parser.parseImageFromPage(html, index)
            return@withContext State.Success(image)
        }
    }

}