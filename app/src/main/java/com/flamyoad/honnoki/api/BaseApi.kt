package com.flamyoad.honnoki.api

import com.flamyoad.honnoki.model.Manga

interface BaseApi {
    suspend fun searchForLatestManga(index: Int): List<Manga>
    suspend fun searchForTrendingManga(index: Int): List<Manga>
}