package com.flamyoad.honnoki.model

import java.lang.IllegalArgumentException

enum class MangaType(val readableName: String) {
    TRENDING(" Trending"),
    RECENTLY("Most Recent");

    companion object {
        fun fromName(name: String?): MangaType {
            return when (name) {
                TRENDING.readableName -> TRENDING
                RECENTLY.readableName -> RECENTLY
                else -> throw IllegalArgumentException("Invalid enum")
            }
        }
    }
}