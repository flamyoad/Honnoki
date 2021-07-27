package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.api.exception.InvalidGenreException
import com.flamyoad.honnoki.api.handler.ApiRequestHandler
import com.flamyoad.honnoki.api.handler.NetworkResult
import com.flamyoad.honnoki.data.GenreConstants
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.network.SenMangaService
import com.flamyoad.honnoki.parser.SenMangaParser
import com.flamyoad.honnoki.utils.extensions.stringSuspending
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SenMangaApi(
    private val service: SenMangaService,
    private val parser: SenMangaParser,
    private val apiHandler: ApiRequestHandler
) : BaseApi() {

    override val startingPageIndex: Int
        get() = 1

    override suspend fun searchForLatestManga(index: Int): State<List<Manga>> {
        when (val response = apiHandler.safeApiCall { service.getLatestManga(index) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForRecentMangas(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    override suspend fun searchForTrendingManga(index: Int): State<List<Manga>> {
        when (val response = apiHandler.safeApiCall { service.getTrendingManga(index) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForTrendingMangas(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    suspend fun searchForOverview(link: String): State<MangaOverview> {
        when (val response = apiHandler.safeApiCall { service.getHtml(link) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForMangaOverview(html, link) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    suspend fun searchForGenres(link: String): State<List<Genre>> {
        when (val response = apiHandler.safeApiCall { service.getHtml(link) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForGenres(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    suspend fun searchForAuthors(link: String): State<List<Author>> {
        when (val response = apiHandler.safeApiCall { service.getHtml(link) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForAuthors(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    suspend fun searchForChapterList(link: String): State<List<Chapter>> {
        when (val response = apiHandler.safeApiCall { service.getHtml(link) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForChapterList(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    suspend fun searchForImageList(link: String): State<List<Page>> {
        when (val response = apiHandler.safeApiCall { service.getHtml(link) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForImageList(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    override suspend fun searchByKeyword(keyword: String, index: Int): State<List<SearchResult>> {
        when (val response = apiHandler.safeApiCall { service.searchByKeyword(keyword, index) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForSearchByKeyword(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    override suspend fun searchByKeywordAndGenres(
        keyword: String,
        genre: GenreConstants,
        index: Int
    ): State<List<SearchResult>> {

        if (genre == GenreConstants.ALL) {
            throw InvalidGenreException("There is no id for `All` genres! Use searchByKeyword() instead of searchByKeywordAndGenres()")
        }

        val genreString = getSenmangaGenreString(genre)
        if (genreString == "") {
            return State.Success(emptyList())
        }

        val response = apiHandler.safeApiCall {
            service.searchByKeywordAndGenres(genre = genreString, keyword = keyword, index = index)
        }

        when (response) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForSearchByKeywordAndGenre(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    override suspend fun searchMangaByAuthor(param: String, index: Int): State<List<SearchResult>> {
        val link = param + "/?page=${index}"
        when (val response = apiHandler.safeApiCall { service.getHtml(link) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForSearchByKeyword(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    override suspend fun searchMangaByGenre(param: String, index: Int): State<List<SearchResult>> {
        val link = param + "/?page=${index}"
        when (val response = apiHandler.safeApiCall { service.getHtml(link) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForSearchByKeywordAndGenre(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
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