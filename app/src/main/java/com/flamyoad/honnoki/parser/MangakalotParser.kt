package com.flamyoad.honnoki.parser

import com.flamyoad.honnoki.model.*
import org.jsoup.Jsoup

class MangakalotParser {

    fun parseForRecentMangas(html: String?): List<Manga> {
        if (html.isNullOrBlank()) return emptyList()

        val document = Jsoup.parse(html)
        val mangaDivs = document.select(".content-genres-item")

        val mangaList = mutableListOf<Manga>()
        for (div in mangaDivs) {
            val nameDiv = div.selectFirst(".genres-item-name")
            val title = nameDiv.text()
            val link = nameDiv.attr("href")

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

        return mangaList
    }

    fun parseForTrendingManga(html: String?): List<Manga> {
        if (html.isNullOrBlank()) return emptyList()

        val document = Jsoup.parse(html)
        val mangaDivs = document.select(".content-genres-item")

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
        return mangaList
    }

    fun parseForMangaOverview(html: String?, link: String): MangaOverview {
        if (html.isNullOrBlank()) {
            return MangaOverview.empty()
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

        return MangaOverview(
            id = null,
            coverImage = coverImage,
            mainTitle = mainTitle,
            alternativeTitle = alternativeTitle,
            summary = summary,
            status = status,
            source = Source.MANGAKALOT,
            link = link
        )
    }

    fun parseForAuthors(html: String?): List<Author> {
        if (html.isNullOrBlank()) {
            return emptyList()
        }

        val document = Jsoup.parse(html)

        val authors = document.selectFirst("i.info-author")
            .ancestorNonNull(2)
            .select("td.table-value > a")
            .map {
                return@map Author(
                    name = it.textNonNull(),
                    link = it.attrNonNull("href")
                )
            }

        return authors
    }

    fun parseForGenres(html: String?): List<Genre> {
        if (html.isNullOrBlank()) {
            return emptyList()
        }

        val document = Jsoup.parse(html)
        val genres = document.selectFirst("i.info-genres")
            .ancestorNonNull(2)
            .select("td.table-value > a")
            .map {
                return@map Genre(
                    name = it.textNonNull(),
                    link = it.attrNonNull("href")
                )
            }

        return genres
    }

    fun parseForChapterList(html: String?): List<Chapter> {
        if (html.isNullOrBlank()) {
            return emptyList()
        }

        val document = Jsoup.parse(html)

        val chapterList = document.select(".row-content-chapter > li")
        return chapterList.mapIndexed { index, it ->
            val chapterLink = it.selectFirst(".chapter-name")
            Chapter(
                title = chapterLink.textNonNull(),
                number = (chapterList.size - (index + 1)).toDouble(),
                link = chapterLink.attrNonNull("href"),
                date = it.selectFirst(".chapter-time").textNonNull()
            )
        }
    }

    fun parseForImageList(html: String?): List<Page> {
        if (html.isNullOrBlank()) {
            return emptyList()
        }

        val document = Jsoup.parse(html)
        val imageList = document.select("div.container-chapter-reader > img")

        return imageList.mapIndexed { index, element ->
            Page(
                link = element.attrNonNull("src"),
                number = index + 1 // Add 1 to change the value into one-based numbering
            )
        }
    }

    fun parseForSearchByKeyword(html: String?): List<SearchResult> {
        if (html.isNullOrBlank()) {
            return emptyList()
        }

        val document = Jsoup.parse(html)

        val searchResult = document.select(".search-story-item")
        return searchResult.map {
            SearchResult(
                coverImage = it.selectFirst(".img-loading").attrNonNull("src"),
                link = it.selectFirst(".item-title").attrNonNull("href"),
                title = it.selectFirst(".item-title").textNonNull(),
                latestChapter = it.selectFirst(".item-chapter").textNonNull(),
                author = it.selectFirst(".item-author").textNonNull(),
            )
        }
    }
}