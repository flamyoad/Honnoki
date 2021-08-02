package com.flamyoad.honnoki.ui.overview.model

data class LanguageFilter(
    val locale: String,
    val isSelected: Boolean
) {
    companion object {
        fun empty(): LanguageFilter {
            return LanguageFilter(locale = "", isSelected = false)
        }

        fun english(): LanguageFilter {
            return LanguageFilter(locale = "en", isSelected = true)
        }
    }
}
