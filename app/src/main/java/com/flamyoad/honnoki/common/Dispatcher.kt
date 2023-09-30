package com.flamyoad.honnoki.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface Dispatcher {
    fun io(): CoroutineDispatcher
    fun computation(): CoroutineDispatcher
    fun ui(): CoroutineDispatcher
}

class CoroutineDispatcherImpl : Dispatcher {
    override fun io(): CoroutineDispatcher
        = Dispatchers.IO

    override fun computation(): CoroutineDispatcher
        = Dispatchers.Default

    override fun ui(): CoroutineDispatcher
        = Dispatchers.Main
}
