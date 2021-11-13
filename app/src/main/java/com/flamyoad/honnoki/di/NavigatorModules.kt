package com.flamyoad.honnoki.di

import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.common.navigator.Navigator
import com.flamyoad.honnoki.common.navigator.NavigatorImpl
import org.koin.dsl.module

@ExperimentalPagingApi
val navigatorModules = module {
    single<Navigator> { NavigatorImpl(get()) }
}