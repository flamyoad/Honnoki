package com.flamyoad.honnoki.di

import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.dialog.*
import com.flamyoad.honnoki.ui.home.mangalist.HomeListViewModel
import com.flamyoad.honnoki.ui.home.HomeViewModel
import com.flamyoad.honnoki.ui.library.LibraryViewModel
import com.flamyoad.honnoki.ui.library.bookmark.BookmarkViewModel
import com.flamyoad.honnoki.ui.library.history.ReadHistoryViewModel
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
    viewModel { HomeViewModel(get()) }

    viewModel { (source: String) -> HomeListViewModel(get(), get(named(source))) }

    viewModel { LibraryViewModel() }

    viewModel { BookmarkViewModel(get(), get(), get(named(KoinConstants.APP_SCOPE))) }

    viewModel { ReadHistoryViewModel(get(), get(), get(named(KoinConstants.APP_SCOPE))) }

    viewModel { (source: String) -> MangaOverviewViewModel(get(), get(named(source))) }

    viewModel { (source: String) ->
        ReaderViewModel(
            get(),
            get(),
            get(),
            get(named(KoinConstants.APP_SCOPE)),
            get(named(source))
        )
    }

    viewModel { VerticalScrollingReaderViewModel() }

    viewModel {
        SimpleSearchViewModel(
            androidApplication(),
            get(),
            get(named(KoinConstants.MANGAKALOT))
        )
    }

    // Dialogs
    viewModel { AddBookmarkGroupDialogViewModel(androidApplication(), get()) }
    viewModel { BookmarkDialogViewModel(androidApplication(), get()) }
    viewModel { ChangeBookmarkGroupNameViewModel(androidApplication(), get()) }
    viewModel { DeleteBookmarkGroupDialogViewModel(androidApplication(), get()) }
    viewModel { MoveBookmarkDialogViewModel(get(), get(), get(named(KoinConstants.APP_SCOPE))) }
}