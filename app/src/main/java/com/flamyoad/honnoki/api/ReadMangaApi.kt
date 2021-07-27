package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.api.exception.InvalidGenreException
import com.flamyoad.honnoki.api.handler.ApiRequestHandler
import com.flamyoad.honnoki.api.handler.NetworkResult
import com.flamyoad.honnoki.data.GenreConstants
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.entities.*
import com.flamyoad.honnoki.network.ReadMangaService
import com.flamyoad.honnoki.parser.ReadMangaParser
import com.flamyoad.honnoki.utils.extensions.stringSuspending
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ReadMangaApi(
    private val service: ReadMangaService,
    private val parser: ReadMangaParser,
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
                return successOrErrorIfNull { parser.parseForPageList(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    override suspend fun searchByKeyword(keyword: String, index: Int): State<List<SearchResult>> {
        // POST request gives all result in oneshot. So just return empty list on index > 1
        if (index > 1) {
            return State.Success(emptyList())
        }

        when (val response = apiHandler.safeApiCall { service.searchByKeyword(keyword) }) {
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

        if (index > 1) {
            return State.Success(emptyList())
        }

        val response = apiHandler.safeApiCall {
            service.searchByKeywordAndGenre(query = keyword, genreId = getReadMngGenreId(genre))
        }
        when (response) {
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
        // https://www.readmng.com/category/action/1
        val url = "$param/$index"

        when (val response = apiHandler.safeApiCall { service.getHtml(url) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForSearchByKeyword(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    override suspend fun searchMangaByAuthor(param: String, index: Int): State<List<SearchResult>> {
        if (index > 1) {
            return State.Success(emptyList())
        }

        when (val response = apiHandler.safeApiCall { service.getHtml(param) }) {
            is NetworkResult.Success -> {
                val html = response.data.stringSuspending()
                return successOrErrorIfNull { parser.parseForSearchByAuthor(html) }
            }
            is NetworkResult.Failure -> {
                return State.Error(response.exception)
            }
        }
    }

    companion object {
        private const val INVALID_GENRE = -1

        /**
         * Get the id of genre in ReadMng's database
         */
        fun getReadMngGenreId(genre: GenreConstants): Int {
            return when (genre) {
                GenreConstants.ALL -> throw InvalidGenreException("This genre constant does not have its own id")
                GenreConstants.ACTION -> 2
                GenreConstants.ADULT -> INVALID_GENRE
                GenreConstants.ADVENTURE -> 4
                GenreConstants.COMEDY -> 5
                GenreConstants.COOKING -> INVALID_GENRE
                GenreConstants.DOUJINSHI -> INVALID_GENRE
                GenreConstants.DRAMA -> 7
                GenreConstants.ECCHI -> 8
                GenreConstants.FANTASY -> 9
                GenreConstants.GENDER_BENDER -> 10
                GenreConstants.HAREM -> 11
                GenreConstants.HISTORICAL -> 12
                GenreConstants.HORROR -> 13
                GenreConstants.ISEKAI -> INVALID_GENRE
                GenreConstants.JOSEI -> 14
                GenreConstants.MANHUA -> INVALID_GENRE
                GenreConstants.MANHWA -> INVALID_GENRE
                GenreConstants.MARTIAL_ARTS -> 16
                GenreConstants.MATURE -> 17
                GenreConstants.MECHA -> 18
                GenreConstants.MEDICAL -> INVALID_GENRE
                GenreConstants.MYSTERY -> 19
                GenreConstants.ONE_SHOT -> 20
                GenreConstants.PSYCHOLOGICAL -> 21
                GenreConstants.ROMANCE -> 22
                GenreConstants.SCHOOL_LIFE -> 23
                GenreConstants.SCIFI -> 24
                GenreConstants.SEINEN -> 25
                GenreConstants.SHOUJO -> 27
                GenreConstants.SHOUJO_AI -> 28
                GenreConstants.SHOUNEN -> 29
                GenreConstants.SHOUNEN_AI -> 30
                GenreConstants.SLICE_OF_LIFE -> 31
                GenreConstants.SMUT -> 32
                GenreConstants.SPORTS -> 33
                GenreConstants.SUPERNATURAL -> 34
                GenreConstants.TRAGEDY -> 35
                GenreConstants.WEBTOONS -> INVALID_GENRE
                GenreConstants.YAOI -> 36
                GenreConstants.YURI -> 37
            }
        }

        // https://www.readmng.com/category/action/1
        fun getReadMngGenreUrl(genre: GenreConstants): String {
            return when (genre) {
                GenreConstants.ACTION -> "https://www.readmng.com/category/action"
                GenreConstants.ADVENTURE -> "https://www.readmng.com/category/adventure"
                GenreConstants.COMEDY -> "https://www.readmng.com/category/comedy"
                GenreConstants.DOUJINSHI -> "https://www.readmng.com/category/doujinshi"
                GenreConstants.DRAMA -> "https://www.readmng.com/category/drama"
                GenreConstants.ECCHI -> "https://www.readmng.com/category/ecchi"
                GenreConstants.FANTASY -> "https://www.readmng.com/category/fantasy"
                GenreConstants.GENDER_BENDER -> "https://www.readmng.com/category/gender-bender"
                GenreConstants.HAREM -> "https://www.readmng.com/category/harem"
                GenreConstants.HISTORICAL -> "https://www.readmng.com/category/historical"
                GenreConstants.HORROR -> "https://www.readmng.com/category/horror"
                GenreConstants.ISEKAI -> "https://www.readmng.com/category/isekai"
                GenreConstants.JOSEI -> "https://www.readmng.com/category/josei"
                GenreConstants.MARTIAL_ARTS -> "https://www.readmng.com/category/martial-arts"
                GenreConstants.MATURE -> "https://www.readmng.com/category/mature"
                GenreConstants.MECHA -> "https://www.readmng.com/category/mecha"
                GenreConstants.MYSTERY -> "https://www.readmng.com/category/mystery"
                GenreConstants.ONE_SHOT -> "https://www.readmng.com/category/one-shot"
                GenreConstants.PSYCHOLOGICAL -> "https://www.readmng.com/category/psychological"
                GenreConstants.ROMANCE -> "https://www.readmng.com/category/romance"
                GenreConstants.SCHOOL_LIFE -> "https://www.readmng.com/category/school-life"
                GenreConstants.SCIFI -> "https://www.readmng.com/category/sci-fi"
                GenreConstants.SEINEN -> "https://www.readmng.com/category/seinen"
                GenreConstants.SHOUJO -> "https://www.readmng.com/category/shoujo"
                GenreConstants.SHOUJO_AI -> "https://www.readmng.com/category/shoujo-ai"
                GenreConstants.SHOUNEN -> "https://www.readmng.com/category/shounen"
                GenreConstants.SHOUNEN_AI -> "https://www.readmng.com/category/shounen-ai"
                GenreConstants.SLICE_OF_LIFE -> "https://www.readmng.com/category/slice-of-life"
                GenreConstants.SMUT -> "https://www.readmng.com/category/smut"
                GenreConstants.SPORTS -> "https://www.readmng.com/category/sports"
                GenreConstants.SUPERNATURAL -> "https://www.readmng.com/category/supernatural"
                GenreConstants.TRAGEDY -> "https://www.readmng.com/category/tragedy"
                GenreConstants.YAOI -> "https://www.readmng.com/category/yaoi"
                GenreConstants.YURI -> "https://www.readmng.com/category/yuri"
                else -> ""
            }
        }
    }
}