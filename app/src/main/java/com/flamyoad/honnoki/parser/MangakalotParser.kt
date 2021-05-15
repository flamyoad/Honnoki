package com.flamyoad.honnoki.parser

import com.flamyoad.honnoki.model.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

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

        val coverImage =
            mangaInfo.selectFirst("div.story-info-left > span.info-image > img").attrNonNull("src")

        val mainTitle = mangaInfo.selectFirst("div.story-info-right > h1").textNonNull()

        // If the manga has no alternative title, the element is not present in the HTML
        val alternativeTitle = mangaInfo.selectFirst("i.info-alternative").parentNonNull().parentNonNull()
            .selectFirst("td.table-value")
            .textNonNull()


        val authors = mangaInfo.selectFirst("i.info-author").parentNonNull().parentNonNull()
            .select("td.table-value > a")
            .map {
                return@map Author(
                    name = it.textNonNull(),
                    link = it.attrNonNull("href")
                )
            }

        val status = mangaInfo.selectFirst("i.info-status").parentNonNull().parentNonNull()
            .selectFirst("td.table-value")
            .textNonNull()

        val genres = mangaInfo.selectFirst("i.info-genres").parentNonNull().parentNonNull()
            .select("td.table-value > a")
            .map {
                return@map Genre(
                    name = it.textNonNull(),
                    link = it.attrNonNull("href")
                )
            }

        return MangaOverview(
            id = null,
            coverImage = coverImage,
            mainTitle = mainTitle,
            alternativeTitle = alternativeTitle,
            summary = summary,
            authors = authors,
            status = status,
            genres = genres,
            source = Source.MANGAKALOT,
            link = link
        )
    }

    fun parseForChapterList(html: String?): List<Chapter> {
        if (html.isNullOrBlank()) {
            return emptyList()
        }

        val document = Jsoup.parse(html)

        val chapterList = document.select(".row-content-chapter > li")
        return chapterList.map {
            val chapterLink = it.selectFirst(".chapter-name")
            Chapter(
                title = chapterLink.textNonNull(),
                link = chapterLink.attrNonNull("href"),
                date = it.selectFirst(".chapter-time").textNonNull()
            )
        }
    }
}