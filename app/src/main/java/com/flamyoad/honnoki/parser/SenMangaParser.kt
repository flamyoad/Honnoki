package com.flamyoad.honnoki.parser

import com.flamyoad.honnoki.model.Manga
import com.flamyoad.honnoki.model.MangaType
import com.flamyoad.honnoki.model.Source
import org.jsoup.Jsoup

class SenMangaParser {

    fun parseForRecentMangas(html: String?): List<Manga> {
        if (html == null) return emptyList()

        val document = Jsoup.parse(html)
        val mangaDivs = document.select("ul.directory > li.series")

        val mangaList = mutableListOf<Manga>()
        for (div in mangaDivs) {
            val title = div.selectFirst(".title > a").text()
            val link = div.selectFirst("a.cover").attr("href")
            val latestChapter = div.selectFirst(".latest-chapter").text()
            val coverImage = div.selectFirst(".cover > img").attr("src")

            mangaList.add(
                Manga(
                    title = title,
                    link = link,
                    latestChapter = latestChapter,
                    coverImage = coverImage,
                    viewCount = -1, // Non existent in senmanga
                    source = Source.SENMANGA,
                    type = MangaType.RECENTLY
                )
            )
        }

        return mangaList
    }
}