package com.flamyoad.honnoki.di

import android.app.Application
import android.content.Context
import com.flamyoad.honnoki.MyApplication
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.experimental.builder.single

val scopeModules = module {
    single(named(KoinConstants.APP_SCOPE)) { provideApplicationScope(androidApplication()) }
}

fun provideApplicationScope(application: Application): CoroutineScope {
    return (application as MyApplication).applicationScope
}