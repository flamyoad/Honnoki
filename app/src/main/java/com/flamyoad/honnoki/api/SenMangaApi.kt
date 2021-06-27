package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.api.exception.InvalidGenreException
import com.flamyoad.honnoki.data.GenreConstants
import com.flamyoad.honnoki.data.State
import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.network.MangakalotService
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
        val response = service.getTrendingManga(index)

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

    override suspend fun searchByKeywordAndGenres(
        keyword: String,
        genre: GenreConstants,
        index: Int
    ): List<SearchResult> {

        if (genre == GenreConstants.ALL) {
            throw InvalidGenreException("There is no id for `All` genres! Use searchByKeyword() instead of searchByKeywordAndGenres()")
        }

        val genreString = getSenmangaGenreString(genre)
        if (genreString == "") {
            return emptyList()
        }

        val response = service.searchByKeywordAndGenres(
            genre = genreString,
            keyword = keyword,
            index = index
        )

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val searchResultList = parser.parseForSearchByKeywordAndGenre(html)

            return@withContext searchResultList
        }
    }

    override suspend fun searchMangaByAuthor(param: String, index: Int): List<SearchResult> {
        val link = param + "/?page=${index}"
        val response = service.getHtml(link)

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val searchResultList = parser.parseForSearchByKeyword(html)

            return@withContext searchResultList
        }
    }

    override suspend fun searchMangaByGenre(param: String, index: Int): List<SearchResult> {
        val link = param + "/?page=${index}"
        val response = service.getHtml(link)

        return withContext(Dispatchers.Default) {
            val html = response.string()
            val searchResultList = parser.parseForSearchByKeywordAndGenre(html)

            return@withContext searchResultList
        }
    }

    companion object {
        /**
         * Constructs an URL pointing to the website that contains manga of selected genre
         * Example: https://raw.senmanga.com/search/genre/Comedy
         */
        fun getSenmangaGenreUrl(genre: GenreConstants): String {
            val genreString = getSenmangaGenreString(genre)
            if (genreString.isBlank()) return ""

            return SenMangaService.BASE_URL + "/search/genre/$genreString"
        }

        /**
         * Get the name of genre in Senmanga's database
         */
        fun getSenmangaGenreString(genre: GenreConstants): String {
            return when (genre) {
                GenreConstants.ALL -> throw InvalidGenreException()
                GenreConstants.ACTION -> "Action"
                GenreConstants.ADULT -> "Adult"
                GenreConstants.ADVENTURE -> "Adventure"
                GenreConstants.COMEDY -> "Comedy"
                GenreConstants.COOKING -> "Cooking"
                GenreConstants.DOUJINSHI -> ""
                GenreConstants.DRAMA -> "Drama"
                GenreConstants.ECCHI -> "Ecchi"
                GenreConstants.FANTASY -> "Fantasy"
                GenreConstants.GENDER_BENDER -> "Gender Bender"
                GenreConstants.HAREM -> "Harem"
                GenreConstants.HISTORICAL -> "Historical"
                GenreConstants.HORROR -> "Horror"
                GenreConstants.ISEKAI -> ""
                GenreConstants.JOSEI -> "Josei"
                GenreConstants.MANHUA -> ""
                GenreConstants.MANHWA -> ""
                GenreConstants.MARTIAL_ARTS -> "Martial Arts"
                GenreConstants.MATURE -> "Mature"
                GenreConstants.MECHA -> ""
                GenreConstants.MEDICAL -> ""
                GenreConstants.MYSTERY -> "Mystery"
                GenreConstants.ONE_SHOT -> ""
                GenreConstants.PSYCHOLOGICAL -> "Psychological"
                GenreConstants.ROMANCE -> "Romance"
                GenreConstants.SCHOOL_LIFE -> "School Life"
                GenreConstants.SCIFI -> "Sci-Fi"
                GenreConstants.SEINEN -> "Seinen"
                GenreConstants.SHOUJO -> "Shoujo"
                GenreConstants.SHOUJO_AI -> "Shoujo Ai"
                GenreConstants.SHOUNEN -> "Shounen"
                GenreConstants.SHOUNEN_AI -> "Shounen Ai"
                GenreConstants.SLICE_OF_LIFE -> "Slice of Life"
                GenreConstants.SMUT -> "Smut"
                GenreConstants.SPORTS -> "Sports"
                GenreConstants.SUPERNATURAL -> "Supernatural"
                GenreConstants.TRAGEDY -> "Tragedy"
                GenreConstants.WEBTOONS -> "Webtoons"
                GenreConstants.YAOI -> "Yaoi"
                GenreConstants.YURI -> "Yuri"
            }
        }
    }
}