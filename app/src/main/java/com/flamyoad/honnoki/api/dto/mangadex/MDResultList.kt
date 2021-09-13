package com.flamyoad.honnoki.api.dto.mangadex

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MDResultList(
//    @Json(name = "results") val results: List<MDResult>?,
    val result: String?,
    val response: String?,
    val data: List<MDResult>?,
    val limit: Int,
    val offset: Int,
    val total: Int
)