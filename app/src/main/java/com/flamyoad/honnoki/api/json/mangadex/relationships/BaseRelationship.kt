package com.flamyoad.honnoki.api.json.mangadex.relationships

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface BaseRelationship {
    val id: String
    val type: String
}