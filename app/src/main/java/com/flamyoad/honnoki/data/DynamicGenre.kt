package com.flamyoad.honnoki.data

/**
 * Used by MangaDex because genres are dynamic - based on API result
 */
data class DynamicGenre(
    val id: String,
    val name: String,
    val constantValue: GenreConstants? = null,
)
