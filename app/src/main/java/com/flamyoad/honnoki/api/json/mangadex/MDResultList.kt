package com.flamyoad.honnoki.api.json.mangadex

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MDResultList(
    @Json(name = "results") val results: List<MDResult>?,
    @Json(name = "limit") val limit: Int,
    @Json(name = "offset") val offset: Int,
    @Json(name = "total") val total: Int
)