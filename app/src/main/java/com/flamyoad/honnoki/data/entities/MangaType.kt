package com.flamyoad.honnoki.data.entities

import java.lang.IllegalArgumentException

enum class MangaType(val readableName: String) {
    TRENDING(" Trending"),
    RECENTLY("Most Recent"),
    TOP("Top"),
    NEW("New");

    companion object {
        fun fromName(name: String?): MangaType {
            return when (name) {
                TRENDING.readableName -> TRENDING
                RECENTLY.readableName -> RECENTLY
                TOP.readableName -> TOP
                NEW.readableName -> NEW
                else -> throw IllegalArgumentException("Invalid enum")
            }
        }
    }
}