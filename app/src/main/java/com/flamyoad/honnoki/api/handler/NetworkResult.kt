package com.flamyoad.honnoki.api.handler

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Failure(val exception: ApiException, val message: String? = "") : NetworkResult<Nothing>()
}
