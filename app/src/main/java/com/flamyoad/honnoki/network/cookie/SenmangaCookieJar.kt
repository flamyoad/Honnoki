package com.flamyoad.honnoki.network.cookie

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class SenmangaCookieJar: CookieJar {

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {}

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return listOf(createViewerCookie())
    }

    private fun createViewerCookie(): Cookie {
        return Cookie.Builder()
            .domain("raw.senmanga.com")
            .path("/")
            .name("viewer")
            .value("1")
            .httpOnly()
            .build()
    }
}