package com.flamyoad.honnoki.api.dto.mangadex

import com.flamyoad.honnoki.api.dto.mangadex.relationships.BaseRelationship
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MDChapter(
    @Json(name = "results") val results: List<MDChapterResult>,
    @Json(name = "limit") val limit: Int,
    @Json(name = "offset") val offset: Int,
    @Json(name = "total") val total: Int
)

@JsonClass(generateAdapter = true)
data class MDChapterResult(
    @Json(name = "result") val result: String,
    @Json(name = "data") val data: MDChapterData,
    @Json(name = "relationships") val relationships: List<BaseRelationship>
)

@JsonClass(generateAdapter = true)
data class MDChapterData(
    @Json(name = "id") val id: String,
    @Json(name = "type") val type: String,
    @Json(name = "attributes") val attributes: MDChapterAttributes
)

@JsonClass(generateAdapter = true)
data class MDChapterAttributes(
    @Json(name = "volume") val volume: String?,
    @Json(name = "chapter") val chapter: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "translatedLanguage") val translatedLanguage: String?,
    @Json(name = "hash") val hash: String?,
    @Json(name = "data") val data: List<String>,
    @Json(name = "dataSaver") val dataSaver: List<String>,
    @Json(name = "publishAt") val publishAt: String?,
    @Json(name = "createdAt") val createdAt: String?,
    @Json(name = "updatedAt") val updatedAt: String?,
    @Json(name = "version") val version: Int?
)

