package com.flamyoad.honnoki.di

import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.ui.home.HomeViewModel
import com.flamyoad.honnoki.ui.library.bookmark.BookmarkViewModel
import com.flamyoad.honnoki.ui.overview.MangaOverviewViewModel
import com.flamyoad.honnoki.ui.reader.ReaderFrameViewModel
import com.flamyoad.honnoki.ui.reader.ReaderViewModel
import com.flamyoad.honnoki.ui.search.SimpleSearchViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@ExperimentalPagingApi
val viewModelModules = module {
    viewModel { HomeViewModel(androidApplication(), get(), get()) }
    viewModel { BookmarkViewModel(androidApplication(), get()) }
    viewModel { MangaOverviewViewModel(androidApplication(), get(), get()) }
    viewModel { ReaderViewModel(androidApplication(), get(), get()) }
    viewModel { ReaderFrameViewModel(androidApplication()) }
    viewModel { SimpleSearchViewModel(androidApplication(), get(), get()) }
}