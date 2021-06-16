package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.data.model.*
import com.flamyoad.honnoki.network.DM5Service
import com.flamyoad.honnoki.parser.DM5Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DM5Api(
    private val service: DM5Service,
    private val parser: DM5Parser
): BaseApi() {

    companion object {
        const val MAXIMUM_DATE_INDEX = 6
    }

    /**
     * DM5 uses epoch time (13 digits) to fetch recently updated manga from their server
     */
    private var epochTimeMillis = System.currentTimeMillis()

    override val startingPageIndex: Int
        get() = 0

    override suspend fun searchForLatestManga(index: Int): List<Manga> {
        if (index > MAXIMUM_DATE_INDEX) {
            return emptyList()
        }

        val response = service.getLatestManga(epochTimeMillis, index)

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val mangaList = parser.parseForRecentMangas(html)

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

}