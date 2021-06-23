package com.flamyoad.honnoki.network.interceptor

import android.content.Context
import android.webkit.WebSettings
import okhttp3.Interceptor
import okhttp3.Response

class MobileUserAgentInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .removeHeader("User-Agent")
            .addHeader("User-Agent", WebSettings.getDefaultUserAgent(context))
            .build()

        return chain.proceed(request)
    }
}