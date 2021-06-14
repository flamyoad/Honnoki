package com.flamyoad.honnoki.api

import android.util.Log
import com.flamyoad.honnoki.data.model.Genre
import com.flamyoad.honnoki.data.model.Manga
import com.flamyoad.honnoki.data.model.MangaOverview
import com.flamyoad.honnoki.data.model.State
import com.flamyoad.honnoki.network.MangaTownService
import com.flamyoad.honnoki.parser.MangaTownParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MangaTownApi(
    private val service: MangaTownService,
    private val parser: MangaTownParser
): BaseApi() {

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
}