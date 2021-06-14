package com.flamyoad.honnoki.parser

import com.flamyoad.honnoki.data.model.*
import com.flamyoad.honnoki.network.MangaTownService
import org.jsoup.Jsoup
import java.time.LocalDateTime

class MangaTownParser {
    fun parseForRecentMangas(html: String?): List<Manga> {
        if (html == null) return emptyList()

        val document = Jsoup.parse(html)
        val mangaDivs = document.select(".manga_pic_list > li")

        val mangaList = mutableListOf<Manga>()
        for (div in mangaDivs) {
            val title = div.selectFirst(".title > a").textNonNull()
            val latestChapter = div.selectFirst(".new_chapter").textNonNull()
            val coverImage = div.selectFirst(".manga_cover > img").attrNonNull("src")

            val relativeLink = div.selectFirst(".title > a").attrNonNull("href")
            val absoluteLink = MangaTownService.BASE_URL + relativeLink

            mangaList.add(
                Manga(
                    title = title,
                    link = absoluteLink,
                    latestChapter = latestChapter,
                    coverImage = coverImage,
                    viewCount = -1, // Non existent in senmanga
                    source = Source.MANGATOWN,
                    type = MangaType.RECENTLY
                )
            )
        }
        println(mangaList.size)
        return mangaList
    }

    fun parseForTrendingMangas(html: String?): List<Manga> {
        if (html == null) return emptyList()

        val document = Jsoup.parse(html)
        val mangaDivs = document.select(".manga_pic_list > li")

        val mangaList = mutableListOf<Manga>()
        for (div in mangaDivs) {
            val title = div.selectFirst(".title > a").textNonNull()
            val latestChapter = div.selectFirst(".new_chapter").textNonNull()
            val coverImage = div.selectFirst(".manga_cover > img").attrNonNull("src")

            val relativeLink = div.selectFirst(".title > a").attrNonNull("href")
            val absoluteLink = MangaTownService.BASE_URL + relativeLink

            mangaList.add(
                Manga(
                    title = title,
                    link = absoluteLink,
                    latestChapter = latestChapter,
                    coverImage = coverImage,
                    viewCount = -1, // Non existent in senmanga
                    source = Source.MANGATOWN,
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

        val mainTitle = document.selectFirst(".title-top").textNonNull()

        val contentDiv = document.selectFirst(".detail_content")

        val summary = document.selectFirst("li:nth-child(11)").textNonNull()

        val coverImage = contentDiv.selectFirst("div.detail_info.clearfix > img")
            .attrNonNull("src")

        val alternativeTitle = contentDiv.selectFirst("li:nth-child(3)").ownTextNonNull()

        val status = contentDiv.selectFirst("li:nth-child(8)").ownTextNonNull()

        return MangaOverview(
            id = null,
            coverImage = coverImage,
            mainTitle = mainTitle,
            alternativeTitle = alternativeTitle,
            summary = summary,
            status = status,
            source = Source.MANGATOWN,
            link = link,
            lastReadChapterId = -1,
            lastReadDateTime = LocalDateTime.MIN,
            lastReadPageNumber = -1
        )
    }

    fun parseForGenres(html: String?): List<Genre> {
        if (html.isNullOrBlank()) {
            return emptyList()
        }

        val document = Jsoup.parse(html)
        val genres = document.selectFirst("div.detail_info.clearfix > ul > li:nth-child(5)")
            .select("a")
            .map {
                return@map Genre(
                    name = it.textNonNull(),
                    link = it.attrNonNull("href")
                )
            }
        println(genres)
        return genres
    }

}