package com.flamyoad.honnoki.network.interceptor

import android.webkit.WebSettings
import okhttp3.Interceptor
import okhttp3.Response

class RefererInterceptor(private val refererUrl: String): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Referer", refererUrl)
            .build()

        return chain.proceed(request)
    }
}