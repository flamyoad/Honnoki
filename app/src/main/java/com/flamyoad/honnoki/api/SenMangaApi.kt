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

class SenMangaApi(
    private val service: SenMangaService,
    private val parser: SenMangaParser,
    apiHandler: ApiRequestHandler
) : BaseApi(apiHandler) {

    override val startingPageIndex: Int
        get() = 1

    override suspend fun searchForLatestManga(index: Int): State<List<Manga>> {
        return processApiData(
            apiCall = { service.getLatestManga(index) },
            parseData = { parser.parseForRecentMangas(it.stringSuspending()) }
        )
    }

    override suspend fun searchForTrendingManga(index: Int): State<List<Manga>> {
        return processApiData(
            apiCall = { service.getTrendingManga(index) },
            parseData = { parser.parseForTrendingMangas(it.stringSuspending()) }
        )
    }

    suspend fun searchForOverview(link: String): State<MangaOverview> {
        return processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseForMangaOverview(it.stringSuspending(), link) }
        )
    }

    suspend fun searchForGenres(link: String): State<List<Genre>> {
        return processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseForGenres(it.stringSuspending()) }
        )
    }

    suspend fun searchForAuthors(link: String): State<List<Author>> {
        return processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseForAuthors(it.stringSuspending()) }
        )
    }

    suspend fun searchForChapterList(link: String): State<List<Chapter>> {
        return processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseForChapterList(it.stringSuspending()) }
        )
    }

    suspend fun searchForImageList(link: String): State<List<Page>> {
        return processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseForImageList(it.stringSuspending()) }
        )
    }

    override suspend fun searchByKeyword(keyword: String, index: Int): State<List<SearchResult>> {
        return processApiData(
            apiCall = { service.searchByKeyword(keyword, index) },
            parseData = { parser.parseForSearchByKeyword(it.stringSuspending()) }
        )
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

        return processApiData(
            apiCall = { service.searchByKeywordAndGenres(genre = genreString, keyword = keyword, index = index) },
            parseData = { parser.parseForSearchByKeywordAndGenre(it.stringSuspending()) }
        )
    }

    override suspend fun searchMangaByAuthor(param: String, index: Int): State<List<SearchResult>> {
        val link = param + "/?page=${index}"
        return processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseForSearchByKeyword(it.stringSuspending()) }
        )
    }

    override suspend fun searchMangaByGenre(param: String, index: Int): State<List<SearchResult>> {
        val link = param + "/?page=${index}"
        return processApiData(
            apiCall = { service.getHtml(link) },
            parseData = { parser.parseForSearchByKeywordAndGenre(it.stringSuspending()) }
        )
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