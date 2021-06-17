package com.flamyoad.honnoki.network.interceptor

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit


// https://medium.com/@bapspatil/caching-with-retrofit-store-responses-offline-71439ed32fda
// https://blog.mindorks.com/okhttp-interceptor-making-the-most-of-it#:~:text=Application%20Interceptors%3A%20These%20are%20interceptors,Core%20Library%20and%20the%20Server.

const val CACHE_CONTROL_HEADER = "Cache-Control"
const val CACHE_CONTROL_NO_CACHE = "no-cache"

class CacheInterceptor(val maxAge: Int, val timeUnit: TimeUnit) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val originalResponse = chain.proceed(request)

        // If Cache-Control has empty value, it means the requestor has never asked
        // for cache in the first place.
        val emptyHeaderValue = request.header(CACHE_CONTROL_HEADER).isNullOrBlank()
        val noCache = request.header(CACHE_CONTROL_HEADER) == CACHE_CONTROL_NO_CACHE
        if (emptyHeaderValue && noCache){
            return originalResponse
        }

        val cacheControl = CacheControl.Builder()
            .maxAge(maxAge, timeUnit)
            .build()

        // Attach "Cache-Control: max-age" to the response
        return originalResponse.newBuilder()
            .header(CACHE_CONTROL_HEADER, cacheControl.toString())
            .removeHeader("Pragma") // Caching doesnt work if this header is not removed
            .build()
    }
}