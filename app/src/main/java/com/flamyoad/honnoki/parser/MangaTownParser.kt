package com.flamyoad.honnoki.parser

import com.flamyoad.honnoki.data.model.Manga
import com.flamyoad.honnoki.data.model.MangaType
import com.flamyoad.honnoki.data.model.Source
import org.jsoup.Jsoup

class MangaTownParser {
    fun parseForRecentMangas(html: String?): List<Manga> {
        if (html == null) return emptyList()

        val document = Jsoup.parse(html)
        val mangaDivs = document.select(".manga_pic_list > li")

        val mangaList = mutableListOf<Manga>()
        for (div in mangaDivs) {
            val title = div.selectFirst(".title > a").textNonNull()
            val link = div.selectFirst(".title > a").attrNonNull("href")
            val latestChapter = div.selectFirst(".new_chapter").textNonNull()
            val coverImage = div.selectFirst(".manga_cover > img").attrNonNull("src")

            mangaList.add(
                Manga(
                    title = title,
                    link = link,
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
            val link = div.selectFirst(".title > a").attrNonNull("href")
            val latestChapter = div.selectFirst(".new_chapter").textNonNull()
            val coverImage = div.selectFirst(".manga_cover > img").attrNonNull("src")

            mangaList.add(
                Manga(
                    title = title,
                    link = link,
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

}