package com.flamyoad.honnoki.parser

import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.data.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.lang.NullPointerException
import java.time.LocalDateTime

class MangakalotParser {

    suspend fun parseForRecentMangas(html: String?): List<Manga> =
        withContext(Dispatchers.Default) {
            if (html.isNullOrBlank()) return@withContext emptyList()

            val document = Jsoup.parse(html)
            val mangaDivs = document.select(".content-genres-item")
                ?: return@withContext emptyList()

            val mangaList = mutableListOf<Manga>()
            for (div in mangaDivs) {
                val nameDiv = div.selectFirst(".genres-item-name")
                val title = nameDiv.textNonNull()
                val link = nameDiv.attrNonNull("href")

                val coverImage = div.selectFirst(".genres-item-img > img").attrNonNull("src")
                val latestChapter = div.selectFirst(".genres-item-chap").textNonNull()
                val viewCount = div.selectFirst(".genres-item-view").textNonNull()

                mangaList.add(
                    Manga(
                        title = title,
                        link = link,
                        latestChapter = latestChapter,
                        coverImage = coverImage,
                        viewCount = viewCount.toIntOrNull() ?: 0,
                        source = Source.MANGAKALOT,
                        type = MangaType.RECENTLY
                    )
                )
            }

            return@withContext mangaList
        }

    suspend fun parseForTrendingManga(html: String?): List<Manga> =
        withContext(Dispatchers.Default) {
            if (html.isNullOrBlank()) return@withContext emptyList()

            val document = Jsoup.parse(html)
            val mangaDivs =
                document.select(".content-genres-item") ?: return@withContext emptyList()

            val mangaList = mutableListOf<Manga>()
            for (div in mangaDivs) {
                val nameDiv = div.selectFirst(".genres-item-name")
                val title = nameDiv.textNonNull()
                val link = nameDiv.attrNonNull("href")

                val coverImage = div.selectFirst(".genres-item-img > img").attrNonNull("src")
                val latestChapter = div.selectFirst(".genres-item-chap").textNonNull()
                val viewCount = div.selectFirst(".genres-item-view").textNonNull()

                mangaList.add(
                    Manga(
                        title = title,
                        link = link,
                        latestChapter = latestChapter,
                        coverImage = coverImage,
                        viewCount = viewCount.toIntOrNull() ?: 0,
                        source = Source.MANGAKALOT,
                        type = MangaType.TRENDING
                    )
                )
            }
            return@withContext mangaList
        }

    // Not working now because its run inside a JS file
    suspend fun parseForTopManga(html: String?): List<Manga> =
        withContext(Dispatchers.Default) {
            if (html.isNullOrBlank()) return@withContext emptyList()

            val document = Jsoup.parse(html)

            val mangaDivs = document.select("#owl-slider > .item")
                ?: return@withContext emptyList()

            val mangaList = mangaDivs.map {
                val titleDiv = it.selectFirst(".slide-caption > h3 > a")

                val title = titleDiv.attrNonNull("href")
                val link = titleDiv.attrNonNull("title")
                val coverImage = it.selectFirst(".img-loading").attrNonNull("src")
                val latestChapter = it.selectFirst(".slide-caption > a").attrNonNull("title")

                return@map Manga(
                    title = title,
                    link = link,
                    coverImage = coverImage,
                    latestChapter = latestChapter,
                    viewCount = 0, // Can't get view count info from this section
                    source = Source.MANGAKALOT,
                    type = MangaType.TOP
                )

            }.toList()

            return@withContext mangaList
        }

    suspend fun parseForNewManga(html: String?): List<Manga> =
        withContext(Dispatchers.Default) {
            if (html.isNullOrBlank()) return@withContext emptyList()

            val document = Jsoup.parse(html)

            val mangaDivs = document.select(".content-genres-item")
                ?: return@withContext emptyList()

            val mangaList = mutableListOf<Manga>()
            for (div in mangaDivs) {
                val nameDiv = div.selectFirst(".genres-item-name")
                val title = nameDiv.textNonNull()
                val link = nameDiv.attrNonNull("href")

                val coverImage = div.selectFirst(".genres-item-img > img").attrNonNull("src")
                val latestChapter = div.selectFirst(".genres-item-chap").textNonNull()
                val viewCount = div.selectFirst(".genres-item-view").textNonNull()

                mangaList.add(
                    Manga(
                        title = title,
                        link = link,
                        latestChapter = latestChapter,
                        coverImage = coverImage,
                        viewCount = viewCount.toIntOrNull() ?: 0,
                        source = Source.MANGAKALOT,
                        type = MangaType.NEW
                    )
                )
            }
            return@withContext mangaList
        }

    suspend fun parseForMangaOverview(html: String?, link: String): MangaOverview =
        withContext(Dispatchers.Default) {
            if (html.isNullOrBlank()) {
                return@withContext MangaOverview.empty()
            }

            val document = Jsoup.parse(html)

            val summary = document.selectFirst("#panel-story-info-description")
                .ownTextNonNull()

            val mangaInfo = document.selectFirst("div.panel-story-info")

            val coverImage = mangaInfo.selectFirst("div.story-info-left > span.info-image > img")
                .attrNonNull("src")

            val mainTitle = mangaInfo.selectFirst("div.story-info-right > h1").textNonNull()

            // If the manga has no alternative title, the element is not present in the HTML
            val alternativeTitle = mangaInfo.selectFirst("i.info-alternative")
                .ancestorNonNull(2)
                .selectFirst("td.table-value")
                .textNonNull()

            val status = mangaInfo.selectFirst("i.info-status").parentNonNull().parentNonNull()
                .selectFirst("td.table-value")
                .textNonNull()

            return@withContext MangaOverview(
                id = null,
                coverImage = coverImage,
                mainTitle = mainTitle,
                alternativeTitle = alternativeTitle,
                summary = summary,
                status = status,
                source = Source.MANGAKALOT,
                link = link,
                lastReadChapterId = -1,
                lastReadDateTime = LocalDateTime.MIN,
                lastReadPageNumber = -1
            )
        }

    suspend fun parseForAuthors(html: String?): List<Author> =
        withContext(Dispatchers.Default) {
            if (html.isNullOrBlank()) {
                return@withContext emptyList()
            }

            val document = Jsoup.parse(html)

            val authors = try {
                document.selectFirst("i.info-author")
                    .ancestorNonNull(2)
                    .select("td.table-value > a")
                    .map {
                        return@map Author(
                            name = it.textNonNull(),
                            link = it.attrNonNull("href")
                        )
                    }
            } catch (e: NullPointerException) {
                emptyList()
            }

            return@withContext authors
        }

    suspend fun parseForGenres(html: String?): List<Genre> =
        withContext(Dispatchers.Default) {
            if (html.isNullOrBlank()) {
                return@withContext emptyList()
            }

            val document = Jsoup.parse(html)
            val genres = try {
                document.selectFirst("i.info-genres")
                    .ancestorNonNull(2)
                    .select("td.table-value > a")
                    .map {
                        return@map Genre(
                            name = it.textNonNull(),
                            link = it.attrNonNull("href")
                        )
                    }
            } catch (e: NullPointerException) {
                emptyList()
            }

            return@withContext genres
        }

    suspend fun parseForChapterList(html: String?): List<Chapter> =
        withContext(Dispatchers.Default) {
            if (html.isNullOrBlank()) {
                return@withContext emptyList()
            }

            val document = Jsoup.parse(html)

            val chapterList = document.select(".row-content-chapter > li")
                ?: return@withContext emptyList()

            return@withContext chapterList.mapIndexed { index, it ->
                val chapterLink = it.selectFirst(".chapter-name")
                Chapter(
                    title = chapterLink.textNonNull(),
                    number = (chapterList.size - (index + 1)).toDouble(),
                    link = chapterLink.attrNonNull("href"),
                    date = it.selectFirst(".chapter-time").textNonNull(),
                    hasBeenRead = false,
                    hasBeenDownloaded = false,
                    translatedLanguage = "en"
                )
            }
        }

    suspend fun parseForImageList(html: String?): List<Page> =
        withContext(Dispatchers.Default) {
            if (html.isNullOrBlank()) {
                return@withContext emptyList()
            }

            val document = Jsoup.parse(html)
            val imageList = document.select("div.container-chapter-reader > img")
                ?: return@withContext emptyList()

            return@withContext imageList.mapIndexed { index, element ->
                Page(
                    link = element.attrNonNull("src"),
                    number = index + 1 // Add 1 to change the value into one-based numbering
                )
            }
        }

    suspend fun parseForSearchByKeyword(html: String?, index: Int): List<SearchResult> =
        withContext(Dispatchers.Default) {
            if (html.isNullOrBlank()) {
                return@withContext emptyList()
            }

            val document = Jsoup.parse(html)

            val lastPageLink = document.selectFirst(".page-last").attrNonNull("href")

            // Manganato still returns valid result even though the page is out of bound
            // If the result only has one page, the lastPageLink link will not be shown in the website
            // To solve this, we have to verify the current link against the last page link scraped
            if (lastPageLink.isBlank() && index > 1) {
                return@withContext emptyList()
            }

            val lastPageIndex = lastPageLink.substringAfterLast("=").toIntOrNull() ?: Int.MAX_VALUE

            if (index > lastPageIndex) {
                return@withContext emptyList()
            }

            val searchResult = document.select(".search-story-item")
                ?: return@withContext emptyList()

            return@withContext searchResult.map {
                SearchResult(
                    coverImage = it.selectFirst(".img-loading").attrNonNull("src"),
                    link = it.selectFirst(".item-title").attrNonNull("href"),
                    title = it.selectFirst(".item-title").textNonNull(),
                    latestChapter = it.selectFirst(".item-chapter").textNonNull(),
                    author = it.selectFirst(".item-author").textNonNull(),
                )
            }
        }

    suspend fun parseForSearchByKeywordAndGenre(html: String?, index: Int): List<SearchResult> =
        withContext(Dispatchers.Default) {
            if (html.isNullOrBlank()) {
                return@withContext emptyList()
            }

            val document = Jsoup.parse(html)

            val lastPageLink = document.selectFirst(".page-last").attrNonNull("href")

            if (lastPageLink.isBlank() && index > 1) {
                return@withContext emptyList()
            }

            val lastPageIndex = lastPageLink.substringAfterLast("=").toIntOrNull() ?: Int.MAX_VALUE
            if (index > lastPageIndex) {
                return@withContext emptyList()
            }

            val searchResult = document.select(".content-genres-item")
                ?: return@withContext emptyList()
            return@withContext searchResult.map {
                SearchResult(
                    coverImage = it.selectFirst(".img-loading").attrNonNull("src"),
                    link = it.selectFirst(".genres-item-name").attrNonNull("href"),
                    title = it.selectFirst(".genres-item-name").textNonNull(),
                    latestChapter = it.selectFirst(".genres-item-chap").textNonNull(),
                    author = it.selectFirst(".genres-item-author").textNonNull(),
                )
            }
        }
}