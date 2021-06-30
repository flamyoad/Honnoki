package com.flamyoad.honnoki.ui.library.history.model

import com.flamyoad.honnoki.data.entities.ReadHistory
import java.time.LocalDate

/**
 * Presentation model for read history
 */
sealed class ViewReadHistory {
    data class Header(val date: LocalDate): ViewReadHistory()

    data class Item(val history: ReadHistory): ViewReadHistory() {
        val lastReadDate: LocalDate get() = history.overview.lastReadDateTime.toLocalDate()
    }
}
