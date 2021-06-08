package com.flamyoad.honnoki.di

import android.content.Context
import com.flamyoad.honnoki.network.MangakalotService
import com.flamyoad.honnoki.network.SenMangaService
import com.flamyoad.honnoki.network.interceptor.CacheInterceptor
import com.flamyoad.honnoki.network.interceptor.RefererInterceptor
import com.flamyoad.honnoki.network.interceptor.UserAgentInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val networkModules = module {
    single { provideMangakalotService(get(named(KoinConstants.MANGAKALOT))) }
    single { provideSenmangaService(get(named(KoinConstants.SENMANGA))) }

    single<OkHttpClient>(named(KoinConstants.MANGAKALOT)) {
        provideMangakalotHttpClient(androidContext(), get())
    }

    single<OkHttpClient>(named(KoinConstants.SENMANGA)) {
        provideSenmangaHttpClient(androidContext(), get())
    }

    factory { provideHttpLoggingInterceptor() }
}

fun provideMangakalotService(httpClient: OkHttpClient): MangakalotService {
    val retrofit = Retrofit.Builder()
        .baseUrl(MangakalotService.BASE_URL)
        .client(httpClient)
        .build()
    return retrofit.create(MangakalotService::class.java)
}

fun provideSenmangaService(httpClient: OkHttpClient): SenMangaService {
    val retrofit = Retrofit.Builder()
        .baseUrl(SenMangaService.BASE_URL)
        .client(httpClient)
        .build()
    return retrofit.create(SenMangaService::class.java)
}

fun provideMangakalotHttpClient(
    context: Context,
    loggingInterceptor: HttpLoggingInterceptor
): OkHttpClient {
    val myCache = (Cache(context.cacheDir, MangakalotService.CACHE_SIZE))

    return OkHttpClient.Builder()
        .cache(myCache)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(UserAgentInterceptor(context))
        .addInterceptor(RefererInterceptor(MangakalotService.BASE_URL))
        .addNetworkInterceptor(CacheInterceptor(1, TimeUnit.MINUTES))
        .build()
}

fun provideSenmangaHttpClient(
    context: Context,
    loggingInterceptor: HttpLoggingInterceptor
): OkHttpClient {
    val myCache = (Cache(context.cacheDir, MangakalotService.CACHE_SIZE))

    return OkHttpClient.Builder()
        .cache(myCache)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(UserAgentInterceptor(context))
        .addNetworkInterceptor(CacheInterceptor(1, TimeUnit.MINUTES))
        .build()
}

fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }
}