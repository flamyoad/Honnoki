package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.api.json.mangadex.relationships.RelCoverImage
import com.flamyoad.honnoki.data.Source
import com.flamyoad.honnoki.data.entities.Manga
import com.flamyoad.honnoki.data.entities.MangaType
import com.flamyoad.honnoki.network.MangadexService

class MangadexApi(private val service: MangadexService) : BaseApi() {
    override val startingPageIndex: Int
        get() = 0

    override suspend fun searchForLatestManga(index: Int): List<Manga> {
        val offset = index * PAGINATION_SIZE
        val json = service.getRecentlyAddedManga(offset, PAGINATION_SIZE, ORDER_DESC)

        val failToGetResults = json.results?.firstOrNull()?.result != RESULT_OK
        if (failToGetResults) {
            return emptyList()
        }

        val mangas = json.results?.map {
            val mangaId = it.data?.id ?: ""

            /*  If coverImageRelationship is null (cannot be casted),
             *  means it does not have cover image... Lol ask MangaDex
             */
            val relationship = it.relationships?.firstOrNull { it.type == "cover_art" }
            val fileName = if (relationship is RelCoverImage) {
                relationship.getFileName()
            } else {
                ""
            }

            val coverImageUrl = constructCoverImageUrl(mangaId, fileName, CoverImageQuality.WIDTH_512PX)

            Manga(
                title = it.data?.attributes?.title?.en ?: "",
                link = mangaId,
                latestChapter = "",
                coverImage = coverImageUrl,
                viewCount = 0, // Don't care
                source = Source.MANGADEX,
                type = MangaType.RECENTLY
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

    companion object {
        const val PAGINATION_SIZE = 50
        const val ORDER_DESC = "desc"
        const val ORDER_ASC = "asc"
        const val RESULT_OK = "ok"
        const val COVER_ART = "cover_art"
    }
}