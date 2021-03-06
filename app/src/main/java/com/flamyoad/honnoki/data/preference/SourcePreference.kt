package com.flamyoad.honnoki.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.utils.extensions.getConvertedValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SourcePreference(private val dataStore: DataStore<Preferences>) {
    private val HOME_SOURCE = stringPreferencesKey("home_source")
    private val SEARCH_SOURCE = stringPreferencesKey("search_source")

    val homeSource: Flow<Source> = dataStore.getConvertedValue(HOME_SOURCE) {
        Source.valueOf(it ?: Source.MANGAKALOT.toString())
    }

    val searchSource: Flow<Source> = dataStore.getConvertedValue(SEARCH_SOURCE) {
        Source.valueOf(it ?: Source.MANGAKALOT.toString())
    }

    suspend fun editHomeSource(source: Source) = dataStore.edit {
        it[HOME_SOURCE] = source.toString()
    }

    suspend fun editSearchSource(source: Source) = dataStore.edit {
        it[SEARCH_SOURCE] = source.toString()
    }
}