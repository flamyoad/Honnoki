package com.flamyoad.honnoki.parser

import com.flamyoad.honnoki.api.json.mangadex.MangaJson
import com.flamyoad.honnoki.api.json.mangadex.relationships.RelCoverImage
import com.flamyoad.honnoki.data.Source
import com.flamyoad.honnoki.data.entities.Manga
import com.flamyoad.honnoki.data.entities.MangaType

class MangadexParser {

    fun parseForHomePageMangas(json: MangaJson, type: MangaType): List<Manga> {
        val mangas = json.results?.map { it ->
            val mangaId = it.data?.id ?: ""

            /*  If it is null (cannot be casted to RelCoverImage),
             *  means it does not have cover image... Lol ask MangaDex
             */
            val coverImage = it.relationships?.firstOrNull { rel -> rel.type == "cover_art" }
            val coverImageUrl = if (coverImage is RelCoverImage) {
                constructCoverImageUrl(mangaId, coverImage.getFileName(), CoverImageQuality.WIDTH_512PX)
            } else {
                ""
            }

            Manga(
                title = it.data?.attributes?.title?.en ?: "",
                link = mangaId,
                latestChapter = "",
                coverImage = coverImageUrl,
                viewCount = 0, // Don't care
                source = Source.MANGADEX,
                type = type
            )
        }
        return mangas ?: emptyList()
    }

    private fun constructCoverImageUrl(
        mangaId: String,
        fileName: String,
        quality: CoverImageQuality
    ): String {
        return when (quality) {
            CoverImageQuality.BEST -> "https://uploads.mangadex.org/covers/${mangaId}/${fileName}.jpg"
            CoverImageQuality.WIDTH_512PX -> "https://uploads.mangadex.org/covers/${mangaId}/${fileName}.512.jpg"
            CoverImageQuality.WIDTH_256PX -> "https://uploads.mangadex.org/covers/${mangaId}/${fileName}.256.jpg"
        }
    }

    private enum class CoverImageQuality {
        BEST,
        WIDTH_512PX,
        WIDTH_256PX
    }
}