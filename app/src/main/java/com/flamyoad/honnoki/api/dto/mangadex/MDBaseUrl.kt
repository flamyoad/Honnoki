package com.flamyoad.honnoki.api.dto.mangadex

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MDBaseUrl(
    @Json(name = "result") val result: String,
    @Json(name = "baseUrl") val baseUrl: String,
    @Json(name = "chapter") val chapter: MDBaseUrlChapter
)

// https://api.mangadex.org/docs/retrieving-chapter/
@JsonClass(generateAdapter = true)
data class MDBaseUrlChapter(
    @Json(name = "hash") val hash: String,
    @Json(name = "data") val data: List<String> = emptyList(),
    @Json(name = "dataSaver") val dataSaver: List<String> = emptyList(),
)
