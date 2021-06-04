package com.flamyoad.honnoki.network.interceptor

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit


// https://medium.com/@bapspatil/caching-with-retrofit-store-responses-offline-71439ed32fda
// https://blog.mindorks.com/okhttp-interceptor-making-the-most-of-it#:~:text=Application%20Interceptors%3A%20These%20are%20interceptors,Core%20Library%20and%20the%20Server.

const val CACHE_CONTROL_HEADER = "Cache-Control"
const val CACHE_CONTROL_NO_CACHE = "no-cache"

class CacheInterceptor(val maxAge: Int, val timeUnit: TimeUnit): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val originalResponse = chain.proceed(request)

        // Checks if "Cache-Control: no-cache" is present in the request header
        val shouldUseCache = request.header(CACHE_CONTROL_HEADER) != CACHE_CONTROL_NO_CACHE
        if (!shouldUseCache) return originalResponse

        val cacheControl = CacheControl.Builder()
            .maxAge(maxAge, timeUnit)
            .build()

        // Attach "Cache-Control: max-age" to the response
        return originalResponse.newBuilder()
            .header(CACHE_CONTROL_HEADER, cacheControl.toString())
            .build()
    }
}