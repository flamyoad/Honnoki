package com.flamyoad.honnoki.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.flamyoad.honnoki.parser.model.MangadexQualityMode
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.ui.reader.model.PageScrollDirection
import com.flamyoad.honnoki.utils.extensions.getValueBlocking

class ReaderPreference(private val dataStore: DataStore<Preferences>) {
    private val VOLUME_UP_ACTION = stringPreferencesKey("volume_up_action")
    private val VOLUME_DOWN_ACTION = stringPreferencesKey("volume_down_action")
    private val MANGADEX_QUALITY_MODE = stringPreferencesKey("mangadex_quality_mode")
    private val SHOW_ADS = booleanPreferencesKey("show_ads")

    fun shouldShowAds(source: Source): Boolean {
        if (source == Source.MANGADEX) return false
        return dataStore.getValueBlocking(SHOW_ADS) ?: true
    }

    suspend fun editVolumeUpAction(scrollDir: PageScrollDirection) = dataStore.edit {
        it[VOLUME_UP_ACTION] = scrollDir.toString()
    }


    suspend fun editVolumeDownAction(scrollDir: PageScrollDirection) = dataStore.edit {
        it[VOLUME_DOWN_ACTION] = scrollDir.toString()
    }


    suspend fun editMangadexQualityMode(mode: MangadexQualityMode) = dataStore.edit {
        it[MANGADEX_QUALITY_MODE] = mode.toString()
    }


}