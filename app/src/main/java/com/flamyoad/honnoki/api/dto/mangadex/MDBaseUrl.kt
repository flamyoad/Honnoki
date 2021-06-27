package com.flamyoad.honnoki.api.dto.mangadex

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MDBaseUrl(
    @Json(name = "baseUrl") val baseUrl: String
)
