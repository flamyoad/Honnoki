package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.api.exception.InvalidGenreException
import com.flamyoad.honnoki.api.handler.ApiRequestHandler
import com.flamyoad.honnoki.api.handler.NetworkResult
import com.flamyoad.honnoki.network.MangakalotService
import com.flamyoad.honnoki.parser.MangakalotParser
import com.flamyoad.honnoki.data.GenreConstants
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.utils.extensions.stringSuspending
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MangakalotApi(
    private val service: MangakalotService,
    private val parser: MangakalotParser,
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
                return successOrErrorIfNull { parser.parseForTrendingManga(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    override suspend fun searchForTopManga(index: Int): State<List<Manga>> {
        when (val response = apiHandler.safeApiCall { service.getTopWeekManga() }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForTopManga(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    override suspend fun searchForNewManga(index: Int): State<List<Manga>> {
        when (val response = apiHandler.safeApiCall { service.getNewManga(index) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForNewManga(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    suspend fun searchForMangaOverview(link: String): State<MangaOverview> {
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
                return successOrErrorIfNull { parser.parseForSearchByKeyword(html, index) }
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

        val genreString = "_" + getMangakalotGenreId(genre) + "_"

        val response = apiHandler.safeApiCall {
            service.searchByKeywordAndGenres(genre = genreString, keyword = keyword, index = index)
        }

        when (response) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForSearchByKeywordAndGenre(html, index) }
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
                return successOrErrorIfNull { parser.parseForSearchByKeyword(html, index) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    override suspend fun searchMangaByGenre(param: String, index: Int): State<List<SearchResult>> {
        val link = param + "/$index"

        when (val response = apiHandler.safeApiCall { service.getHtml(link) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForSearchByKeywordAndGenre(html, index) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    companion object {
        /**
         * Constructs an URL pointing to the website that contains manga of selected genre
         * Example: https://manganato.com/genre-32
         */
        fun getMangakalotGenreUrl(genre: GenreConstants): String {
            val genreId = getMangakalotGenreId(genre)
            return MangakalotService.BASE_URL + "genre-$genreId"
        }

        /**
         * Get the id of genre in Mangakalot's database
         */
        fun getMangakalotGenreId(genre: GenreConstants): Int {
            return when (genre) {
                GenreConstants.ALL -> throw InvalidGenreException("This genre constant does not have its own id")
                GenreConstants.ACTION -> 2
                GenreConstants.ADULT -> 3
                GenreConstants.ADVENTURE -> 4
                GenreConstants.COMEDY -> 6
                GenreConstants.COOKING -> 7
                GenreConstants.DOUJINSHI -> 9
                GenreConstants.DRAMA -> 10
                GenreConstants.ECCHI -> 11
                GenreConstants.FANTASY -> 12
                GenreConstants.GENDER_BENDER -> 13
                GenreConstants.HAREM -> 14
                GenreConstants.HISTORICAL -> 15
                GenreConstants.HORROR -> 16
                GenreConstants.ISEKAI -> 45
                GenreConstants.JOSEI -> 17
                GenreConstants.MANHUA -> 44
                GenreConstants.MANHWA -> 43
                GenreConstants.MARTIAL_ARTS -> 19
                GenreConstants.MATURE -> 20
                GenreConstants.MECHA -> 21
                GenreConstants.MEDICAL -> 22
                GenreConstants.MYSTERY -> 24
                GenreConstants.ONE_SHOT -> 25
                GenreConstants.PSYCHOLOGICAL -> 26
                GenreConstants.ROMANCE -> 27
                GenreConstants.SCHOOL_LIFE -> 28
                GenreConstants.SCIFI -> 29
                GenreConstants.SEINEN -> 30
                GenreConstants.SHOUJO -> 31
                GenreConstants.SHOUJO_AI -> 32
                GenreConstants.SHOUNEN -> 33
                GenreConstants.SHOUNEN_AI -> 34
                GenreConstants.SLICE_OF_LIFE -> 35
                GenreConstants.SMUT -> 36
                GenreConstants.SPORTS -> 37
                GenreConstants.SUPERNATURAL -> 38
                GenreConstants.TRAGEDY -> 39
                GenreConstants.WEBTOONS -> 40
                GenreConstants.YAOI -> 41
                GenreConstants.YURI -> 42
            }
        }
    }

}