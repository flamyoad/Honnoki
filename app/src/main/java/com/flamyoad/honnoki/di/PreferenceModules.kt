package com.flamyoad.honnoki.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.flamyoad.honnoki.data.preference.SourcePreference
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val preferenceModules = module {
    single { provideDataStore(androidContext()) }

    single { SourcePreference(get()) }
}

fun provideDataStore(context: Context): DataStore<Preferences> {
    val store = PreferenceDataStoreFactory.create {
        context.preferencesDataStoreFile("settings")
    }
    return store
}