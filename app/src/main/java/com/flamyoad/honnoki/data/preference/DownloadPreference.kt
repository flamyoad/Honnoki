package com.flamyoad.honnoki.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.flamyoad.honnoki.utils.extensions.getValueBlocking

class DownloadPreference(private val dataStore: DataStore<Preferences>) {
    private val DOWNLOAD_DIRECTORY_PATH = stringPreferencesKey("download_directory_path")

    fun getDownloadDirectoryPath(): String =
        dataStore.getValueBlocking(DOWNLOAD_DIRECTORY_PATH) ?: ""

    suspend fun setDownloadDirectoryPath(dirPath: String) {
        dataStore.edit { it[DOWNLOAD_DIRECTORY_PATH] = dirPath }
    }
}