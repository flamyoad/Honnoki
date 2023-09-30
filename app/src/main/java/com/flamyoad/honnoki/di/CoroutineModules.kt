package com.flamyoad.honnoki.di

import android.app.Application
import com.flamyoad.honnoki.MyApplication
import com.flamyoad.honnoki.common.CoroutineDispatcherImpl
import com.flamyoad.honnoki.common.Dispatcher
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module

val scopeModules = module {
    single(named(KoinConstants.APP_SCOPE)) { provideApplicationScope(androidApplication()) }
    single { provideCoroutineDispatcher() }
}

fun provideApplicationScope(application: Application): CoroutineScope {
    return (application as MyApplication).applicationScope
}

fun provideCoroutineDispatcher(): Dispatcher {
    return CoroutineDispatcherImpl()
}