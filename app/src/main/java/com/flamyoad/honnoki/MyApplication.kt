package com.flamyoad.honnoki

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class MyApplication: Application() {

    val applicationScope = MainScope()

    override fun onCreate() {
        super.onCreate()
    }
}