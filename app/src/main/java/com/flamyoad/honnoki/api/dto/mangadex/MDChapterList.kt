package com.flamyoad.honnoki.api.dto.mangadex

import com.flamyoad.honnoki.api.dto.mangadex.relationships.BaseRelationship
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MDChapterList(
    val result: String?,
    val response: String?, // Collection
    val data: List<MDChapterData>,
    val limit: Int,
    val offset: Int,
    val total: Int
)

@JsonClass(generateAdapter = true)
data class MDChapterData(
    @Json(name = "id") val id: String,
    @Json(name = "type") val type: String,
    @Json(name = "attributes") val attributes: MDChapterAttributes,
    @Json(name = "relationships") val relationships: List<BaseRelationship>
)

@JsonClass(generateAdapter = true)
data class MDChapterAttributes(
    @Json(name = "volume") val volume: String?,
    @Json(name = "chapter") val chapter: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "translatedLanguage") val translatedLanguage: String?,
    @Json(name = "publishAt") val publishAt: String?,
    @Json(name = "createdAt") val createdAt: String?,
    @Json(name = "updatedAt") val updatedAt: String?,
    @Json(name = "version") val version: Int?
)

