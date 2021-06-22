package com.flamyoad.honnoki.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.flamyoad.honnoki.data.Source
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SourcePreference(private val dataStore: DataStore<Preferences>) {
    private val HOME_SOURCE = stringPreferencesKey("home_source")
    private val SEARCH_SOURCE = stringPreferencesKey("search_source")

    val homeSource: Flow<Source> = dataStore.data.map { prefs ->
        val sourceName = prefs[HOME_SOURCE] ?: Source.MANGAKALOT.toString()
        return@map Source.valueOf(sourceName)
    }

    val searchSource: Flow<Source> = dataStore.data.map { prefs ->
        val sourceName = prefs[SEARCH_SOURCE] ?: Source.MANGAKALOT.toString()
        return@map Source.valueOf(sourceName)
    }

    suspend fun switchHomeSource(source: Source) {
        dataStore.edit { 
            it[HOME_SOURCE] = source.toString()
        }
    }

    suspend fun switchSearchSource(source: Source) {
        dataStore.edit {
            it[SEARCH_SOURCE] = source.toString()
        }
    }
}