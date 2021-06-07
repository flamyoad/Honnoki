package com.flamyoad.honnoki

import android.app.Application
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.di.*
import kotlinx.coroutines.MainScope
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

@ExperimentalPagingApi
class MyApplication: Application() {

    val applicationScope = MainScope()

    override fun onCreate() {
        super.onCreate()

        val appModules = listOf(
            apiModules,
            dbModules,
            networkModules,
            sourceModules,
            viewModelModules
        )

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModules)
        }
    }
}