package com.flamyoad.honnoki.network.interceptor

import android.content.Context
import android.webkit.WebSettings
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Stops the website from redirecting us to mobile-friendly site! We only want the full website
 */
class PCUserAgentInterceptor() : Interceptor {

    private val macChrome = "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.101 Safari/537.36"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .removeHeader("User-Agent")
            .addHeader("User-Agent", macChrome)
            .build()

        return chain.proceed(request)
    }
}