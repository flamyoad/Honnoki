package com.flamyoad.honnoki.data

sealed class State<out T : Any> {
    data class Success<out T : Any>(val value: T) : State<T>()
    data class Error(val exception: Throwable? = null, val message: String = ""): State<Nothing>()
    object Loading : State<Nothing>()
}