package com.flamyoad.honnoki.source.model

enum class TabType(val readableName: String) {
    MOST_RECENT("Most Recent"), // Double-view of Trending + Latest
    TRENDING("Trending"), // Single-view
    LATEST("Latest"), // Single-view
    NEW("New") // Single-view
}

