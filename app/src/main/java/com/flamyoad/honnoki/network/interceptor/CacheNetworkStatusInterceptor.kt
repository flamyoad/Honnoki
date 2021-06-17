package com.flamyoad.honnoki.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Used to check whether response comes from local cache or network
 */
class CacheNetworkStatusInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.networkResponse == null) {
            println("from cache")
        } else {
            println("from network")
        }
        return response
    }
}