package com.flamyoad.honnoki.ui.search.model

import android.content.Context
import com.flamyoad.honnoki.R

data class SearchGenre(
    val name: String,
    val isSelected: Boolean
) {
    companion object {
        /**
         * Returns the default selected item for the genre list in Search screen
         */
        fun getDefaultItem(context: Context): SearchGenre {
            return SearchGenre(
                name = context.getString(R.string.genre_all),
                isSelected = true
            )
        }
    }
}
