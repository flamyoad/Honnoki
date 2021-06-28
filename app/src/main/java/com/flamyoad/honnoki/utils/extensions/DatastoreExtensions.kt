package com.flamyoad.honnoki.utils.extensions

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

/**
 * Blocking read to the preference file. Just like the old androidx Preference
 */
fun <T> DataStore<Preferences>.getValue(key: Preferences.Key<T>): T? =
    runBlocking {
        return@runBlocking data.firstOrNull()?.get(key)
    }