package com.flamyoad.honnoki

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.data.preference.UiPreference
import com.flamyoad.honnoki.di.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import timber.log.Timber

class MyApplication : Application() {

    private val uiPrefs: UiPreference by inject()

    val applicationScope = MainScope()

    @OptIn(ExperimentalPagingApi::class)
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        val appModules = listOf(
            apiModules,
            dbModules,
            networkModules,
            sourceModules,
            repositoryModules,
            scopeModules,
            preferenceModules,
            viewModelModules
        )

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModules)
        }

        applicationScope.launch {
            val nightModeEnabled = uiPrefs.nightModeEnabled.firstOrNull() ?: return@launch
            if (nightModeEnabled) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}