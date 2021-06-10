package com.flamyoad.honnoki.di

import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.dialog.AddBookmarkGroupDialogViewModel
import com.flamyoad.honnoki.dialog.BookmarkDialogViewModel
import com.flamyoad.honnoki.dialog.ChangeBookmarkGroupNameViewModel
import com.flamyoad.honnoki.dialog.DeleteBookmarkGroupDialogViewModel
import com.flamyoad.honnoki.ui.home.HomeViewModel
import com.flamyoad.honnoki.ui.library.LibraryViewModel
import com.flamyoad.honnoki.ui.library.bookmark.BookmarkViewModel
import com.flamyoad.honnoki.ui.overview.MangaOverviewViewModel
import com.flamyoad.honnoki.ui.reader.VerticalScrollingReaderViewModel
import com.flamyoad.honnoki.ui.reader.ReaderViewModel
import com.flamyoad.honnoki.ui.search.SimpleSearchViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

@ExperimentalPagingApi
val viewModelModules = module {
//    viewModel { parameters -> HomeViewModel(get(), get(named(parameters.get()))) }
    viewModel { HomeViewModel(get(), get(named(KoinConstants.MANGAKALOT))) }
    viewModel { LibraryViewModel() }
    viewModel { BookmarkViewModel(get(), get(), get(named(KoinConstants.APP_SCOPE))) }
    viewModel { MangaOverviewViewModel(get(), get(named(KoinConstants.MANGAKALOT))) }
    viewModel { ReaderViewModel(get(), get(), get(named(KoinConstants.MANGAKALOT))) }
    viewModel { VerticalScrollingReaderViewModel() }
    viewModel { SimpleSearchViewModel(androidApplication(), get(), get(named(KoinConstants.MANGAKALOT))) }

    // Dialogs
    viewModel { AddBookmarkGroupDialogViewModel(androidApplication(), get()) }
    viewModel { BookmarkDialogViewModel(androidApplication(), get()) }
    viewModel { ChangeBookmarkGroupNameViewModel(androidApplication(), get()) }
    viewModel { DeleteBookmarkGroupDialogViewModel(androidApplication(), get()) }
}