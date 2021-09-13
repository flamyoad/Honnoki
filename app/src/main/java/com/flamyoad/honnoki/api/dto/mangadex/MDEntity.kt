package com.flamyoad.honnoki.api.dto.mangadex

import com.squareup.moshi.JsonClass

// Single manga
@JsonClass(generateAdapter = true)
data class MDEntity(
    val result: String?,
    val response: String?,
    val data: MDResult?,
)