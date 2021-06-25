package com.flamyoad.honnoki.api.dto.mangadex

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MDCoverImage(
    @Json(name = "results") val results: List<CoverImageListResults>?,
    @Json(name = "limit") val limit: Int?,
    @Json(name = "offset") val offset: Int?,
    @Json(name = "total") val total: Int?
) {
    fun getImageFileByCoverId(coverId: String): String {
        return this.results
            ?.firstOrNull { it.data?.id == coverId }
            ?.data?.attributes?.fileName ?: ""
    }
}

@JsonClass(generateAdapter = true)
data class CoverImageListResults(
    @Json(name = "result") val result: String?,
    @Json(name = "data") val data: CoverImageListData?,
    @Json(name = "relationships") val relationships: List<CoverImageListRelationships>?
)

@JsonClass(generateAdapter = true)
data class CoverImageListData(
    @Json(name = "id") val id: String?,
    @Json(name = "type") val type: String?,
    @Json(name = "attributes") val attributes: CoverImageListAttributes?
)

@JsonClass(generateAdapter = true)
data class CoverImageListAttributes(
    @Json(name = "description") val description: String?,
    @Json(name = "volume") val volume: String?,
    @Json(name = "fileName") val fileName: String?,
    @Json(name = "createdAt") val createdAt: String?,
    @Json(name = "updatedAt") val updatedAt: String?,
    @Json(name = "version") val version: Int?
)

@JsonClass(generateAdapter = true)
data class CoverImageListRelationships(
    @Json(name = "id") val id: String?,
    @Json(name = "type") val type: String?
)
