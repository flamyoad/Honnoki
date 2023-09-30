package com.flamyoad.honnoki.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.flamyoad.honnoki.download.model.NetworkType
import com.flamyoad.honnoki.utils.extensions.getConvertedValue
import com.flamyoad.honnoki.utils.extensions.getValueBlocking
import kotlinx.coroutines.flow.Flow

interface DownloadPreference {
    fun getDownloadDirectoryPath(): String
    fun getPreferredNetworkType(): Flow<NetworkType>

    suspend fun setDownloadDirectoryPath(dirPath: String)
    suspend fun setPreferredNetworkType(networkType: NetworkType)
}

class DownloadPreferenceImpl(
    private val context: Context,
    private val dataStore: DataStore<Preferences>
) : DownloadPreference {

    companion object {
        private val DOWNLOAD_DIRECTORY_PATH = stringPreferencesKey("download_directory_path")
        private val DOWNLOAD_PREFERRED_NETWORK_TYPE = stringPreferencesKey("download_preferred_network_type")
    }

    override fun getDownloadDirectoryPath(): String =
        dataStore.getValueBlocking(DOWNLOAD_DIRECTORY_PATH)
            ?: context.getExternalFilesDir("").toString()

    override fun getPreferredNetworkType(): Flow<NetworkType> {
        return dataStore.getConvertedValue(DOWNLOAD_PREFERRED_NETWORK_TYPE) {
            if (it != null) {
                NetworkType.valueOf(it)
            } else {
                NetworkType.ANY_NETWORK
            }
        }
    }

    override suspend fun setDownloadDirectoryPath(dirPath: String) {
        dataStore.edit { it[DOWNLOAD_DIRECTORY_PATH] = dirPath }
    }

    override suspend fun setPreferredNetworkType(networkType: NetworkType) {
        dataStore.edit {
            it[DOWNLOAD_PREFERRED_NETWORK_TYPE] = networkType.toString()
        }
    }
}