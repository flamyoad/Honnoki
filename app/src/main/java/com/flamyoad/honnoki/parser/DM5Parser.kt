package com.flamyoad.honnoki.parser

import com.flamyoad.honnoki.data.model.Manga
import com.flamyoad.honnoki.data.model.MangaType
import com.flamyoad.honnoki.data.model.Source
import com.flamyoad.honnoki.network.DM5Service
import com.flamyoad.honnoki.parser.json.dm5.DM5JsonAdapter

class DM5Parser(private val jsonAdapter: DM5JsonAdapter) {

    fun parseForRecentMangas(html: String?): List<Manga> {
        if (html.isNullOrBlank())
            return emptyList()

        val jsonModel = jsonAdapter.recentMangaConverter.fromJson(html) ?: return emptyList()

        return jsonModel.items.map {
            Manga(
                title = it.title,
                link =  DM5Service.BASE_URL + it.urlKey,
                latestChapter = it.showLastPartName,
                coverImage = it.showPicUrlB,
                viewCount = 0, // Don't care
                source = Source.DM5,
                type = MangaType.RECENTLY
            )
        }
    }
}