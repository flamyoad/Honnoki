package com.flamyoad.honnoki.utils.extensions

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Returns flow of the preference value. Default value will be used if null shows up.
 */
fun <T> DataStore<Preferences>.getValue(
    key: Preferences.Key<T>,
    defaultValueIfNull: T,
): Flow<T> {
    return data.map { prefs -> prefs[key] ?: defaultValueIfNull }
}

/**
 * Returns flow of the preference value after being converted.
 * Normal use case is to convert [String] into [Enum].
 * The conversion function is responsible to return a default value if null is shown.
 */
fun <T, R> DataStore<Preferences>.getConvertedValue(
    key: Preferences.Key<T>,
    convert: (T?) -> R
): Flow<R> {
    return data.map { prefs -> convert(prefs[key]) }
}

/**
 * Blocking read to the preference file. Just like the old androidx Preference
 */
fun <T> DataStore<Preferences>.getValueBlocking(key: Preferences.Key<T>): T? =
    runBlocking {
        return@runBlocking data.firstOrNull()?.get(key)
    }

/**
 * Blocking write to the preference file.
 * TODO: Check how long it blocks the main thread
 */
fun <T> DataStore<Preferences>.setValueBlocking(key: Preferences.Key<T>, value: T) =
    runBlocking {
        edit { it[key] = value }
    }