package com.flamyoad.honnoki.di

import android.content.Context
import com.flamyoad.honnoki.BuildConfig
import com.flamyoad.honnoki.network.*
import com.flamyoad.honnoki.network.cookie.SenmangaCookieJar
import com.flamyoad.honnoki.network.interceptor.CacheInterceptor
import com.flamyoad.honnoki.network.interceptor.RefererInterceptor
import com.flamyoad.honnoki.network.interceptor.MobileUserAgentInterceptor
import com.flamyoad.honnoki.network.interceptor.PCUserAgentInterceptor
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
    single { provideMangaTownService(get(named(KoinConstants.MANGATOWN))) }
    single { provideReadMangaService(get(named(KoinConstants.READMANGA))) }
    single { provideDM5Service(get(named(KoinConstants.DM5))) }

    single<OkHttpClient>(named(KoinConstants.MANGAKALOT)) {
        provideMangakalotHttpClient(androidContext(), get())
    }

    single<OkHttpClient>(named(KoinConstants.SENMANGA)) {
        provideSenmangaHttpClient(androidContext(), get())
    }

    single<OkHttpClient>(named(KoinConstants.MANGATOWN)) {
        provideMangaTownHttpClient(androidContext(), get())
    }

    single<OkHttpClient>(named(KoinConstants.READMANGA)) {
        provideReadMangaHttpClient(androidContext(), get())
    }

    single<OkHttpClient>(named(KoinConstants.DM5)) {
        provideDM5HttpClient(androidContext(), get())
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

fun provideMangakalotHttpClient(
    context: Context,
    loggingInterceptor: HttpLoggingInterceptor
): OkHttpClient {
    val myCache = (Cache(context.cacheDir, MangakalotService.CACHE_SIZE))

    return OkHttpClient.Builder()
        .cache(myCache)
        .addInterceptor(loggingInterceptor)
        .addNetworkInterceptor(MobileUserAgentInterceptor(context))
        .addNetworkInterceptor(RefererInterceptor(MangakalotService.BASE_URL))
        .addNetworkInterceptor(CacheInterceptor(1, TimeUnit.MINUTES))
        .build()
}

fun provideSenmangaService(httpClient: OkHttpClient): SenMangaService {
    val retrofit = Retrofit.Builder()
        .baseUrl(SenMangaService.BASE_URL)
        .client(httpClient)
        .build()
    return retrofit.create(SenMangaService::class.java)
}

fun provideSenmangaHttpClient(
    context: Context,
    loggingInterceptor: HttpLoggingInterceptor
): OkHttpClient {
    val myCache = (Cache(context.cacheDir, SenMangaService.CACHE_SIZE))

    return OkHttpClient.Builder()
        .cache(myCache)
        .cookieJar(SenmangaCookieJar())
        .addInterceptor(loggingInterceptor)
        .addNetworkInterceptor(MobileUserAgentInterceptor(context))
        .addNetworkInterceptor(CacheInterceptor(1, TimeUnit.MINUTES))
        .build()
}

fun provideMangaTownService(httpClient: OkHttpClient): MangaTownService {
    val retrofit = Retrofit.Builder()
        .baseUrl(MangaTownService.BASE_URL)
        .client(httpClient)
        .build()
    return retrofit.create(MangaTownService::class.java)
}

fun provideMangaTownHttpClient(
    context: Context,
    loggingInterceptor: HttpLoggingInterceptor
): OkHttpClient {
    val myCache = (Cache(context.cacheDir, MangaTownService.CACHE_SIZE))

    return OkHttpClient.Builder()
        .cache(myCache)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(MobileUserAgentInterceptor(context))
        .addNetworkInterceptor(CacheInterceptor(1, TimeUnit.MINUTES))
        .build()
}

fun provideReadMangaHttpClient(
    context: Context,
    loggingInterceptor: HttpLoggingInterceptor
): OkHttpClient {
    val myCache = (Cache(context.cacheDir, ReadMangaService.CACHE_SIZE))

    return OkHttpClient.Builder()
        .cache(myCache)
        .addInterceptor(loggingInterceptor)
        .addNetworkInterceptor(MobileUserAgentInterceptor(context))
        .addNetworkInterceptor(CacheInterceptor(1, TimeUnit.MINUTES))
        .build()
}

fun provideReadMangaService(httpClient: OkHttpClient): ReadMangaService {
    val retrofit = Retrofit.Builder()
        .baseUrl(ReadMangaService.BASE_URL)
        .client(httpClient)
        .build()
    return retrofit.create(ReadMangaService::class.java)
}

fun provideDM5HttpClient(
    context: Context,
    loggingInterceptor: HttpLoggingInterceptor
): OkHttpClient {
    val myCache = (Cache(context.cacheDir, DM5Service.CACHE_SIZE))

    return OkHttpClient.Builder()
        .hostnameVerifier { hostname, session -> true } // HTTP FAILED: javax.net.ssl.SSLPeerUnverifiedException: Hostname dm5.com not verified:
        .cache(myCache)
        .addInterceptor(loggingInterceptor)
        .addNetworkInterceptor(PCUserAgentInterceptor()) // Prevent from redirecting to mobile site
        .addNetworkInterceptor(CacheInterceptor(1, TimeUnit.MINUTES))
        .build()
}

fun provideDM5Service(httpClient: OkHttpClient): DM5Service {
    val retrofit = Retrofit.Builder()
        .baseUrl(DM5Service.BASE_URL)
        .client(httpClient)
        .build()
    return retrofit.create(DM5Service::class.java)
}

fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
    if (BuildConfig.DEBUG)
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
    else
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE

    return httpLoggingInterceptor

}