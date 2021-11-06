package com.flamyoad.honnoki.api.dto.mangadex

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MDTagList(
    val result: String?,
    val response: String?,
    val data: List<MDTag>?,
    val limit: Int,
    val offset: Int,
    val total: Int
)