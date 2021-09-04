package com.flamyoad.honnoki.api.handler

import android.os.Build
import com.flamyoad.honnoki.BuildConfig
import retrofit2.HttpException
import java.io.IOException
import kotlin.runCatching
import com.flamyoad.honnoki.api.handler.ApiException.Code
import com.squareup.moshi.JsonDataException

class ApiRequestHandler {
    suspend inline fun <T> safeApiCall(crossinline apiCall: suspend () -> T): NetworkResult<T> {
        return runCatching {
            apiCall.invoke()
        }.fold(
            onSuccess = { NetworkResult.Success(it) },
            onFailure = { processError(it) }
        )
    }

    fun processError(throwable: Throwable): NetworkResult.Failure {
        return when (throwable) {
            is HttpException -> {
                when (throwable.code()) {
                    400 -> NetworkResult.Failure(ApiException(Code.BadRequest))
                    401 -> NetworkResult.Failure(ApiException(Code.Unauthorized))
                    403 -> NetworkResult.Failure(ApiException(Code.Forbidden))
                    404 -> NetworkResult.Failure(ApiException(Code.UrlNotFound))
                    502 -> NetworkResult.Failure(ApiException(Code.BadGateway))
                }
                NetworkResult.Failure(
                    ApiException(Code.UnspecifiedError),
                    "Error code: ${throwable.code()}, Message: ${throwable.message()}"
                )
            }
            is IOException -> {
                NetworkResult.Failure(ApiException(Code.NoInternetConnection))
            }
            is JsonDataException -> {
                if (BuildConfig.DEBUG) {
                    throw throwable
                }
                NetworkResult.Failure(ApiException(Code.JsonDataMismatch))
            }
            else -> NetworkResult.Failure(
                ApiException(Code.UnspecifiedError),
                "Unknown error: ${throwable.message}"
            )
        }
    }
}