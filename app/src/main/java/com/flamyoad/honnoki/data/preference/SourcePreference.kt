package com.flamyoad.honnoki.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.flamyoad.honnoki.data.model.Source
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SourcePreference(private val dataStore: DataStore<Preferences>) {
    private val SOURCE = stringPreferencesKey("source")

    val source: Flow<Source> = dataStore.data.map { prefs ->
        val sourceName = prefs[SOURCE] ?: Source.MANGAKALOT.toString()
        return@map Source.valueOf(sourceName)
    }

    suspend fun switchSource(source: Source) {
        dataStore.edit { 
            it[SOURCE] = source.toString()
        }
    }
}