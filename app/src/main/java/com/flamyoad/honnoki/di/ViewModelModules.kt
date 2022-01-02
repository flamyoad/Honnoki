package com.flamyoad.honnoki.di

import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.MainViewModel
import com.flamyoad.honnoki.dialog.*
import com.flamyoad.honnoki.ui.download.DownloadPickerViewModel
import com.flamyoad.honnoki.ui.home.mangalist.MangaListViewModel
import com.flamyoad.honnoki.ui.home.HomeViewModel
import com.flamyoad.honnoki.ui.home.dialog.GenrePickerViewModel
import com.flamyoad.honnoki.ui.library.bookmark.BookmarkViewModel
import com.flamyoad.honnoki.ui.library.history.ReadHistoryViewModel
import com.flamyoad.honnoki.ui.lookup.MangaLookupViewModel
import com.flamyoad.honnoki.ui.lookup.model.LookupType
import com.flamyoad.honnoki.ui.options.OptionsViewModel
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
    viewModel { MainViewModel() }

    viewModel { HomeViewModel(get()) }

    viewModel { (source: String) ->
        MangaListViewModel(
            get(),
            get(named(source))
        )
    }

    viewModel {
        BookmarkViewModel(
            get(),
            get(),
            get(named(KoinConstants.APP_SCOPE))
        )
    }

    viewModel {
        ReadHistoryViewModel(
            get(),
            get(),
            get(named(KoinConstants.APP_SCOPE))
        )
    }

    viewModel { (params: String, source: String, lookupType: LookupType) ->
        MangaLookupViewModel(
            params,
            get(named(source)),
            lookupType,
        )
    }

    viewModel { (source: String) ->
        MangaOverviewViewModel(
            get(),
            get(named(source)),
            get()
        )
    }

    viewModel { (source: String) ->
        ReaderViewModel(
            get(),
            get(),
            get(),
            get(named(KoinConstants.APP_SCOPE)),
            get(named(source)),
            get(),
            get(),
        )
    }

    viewModel { VerticalScrollingReaderViewModel(get()) }

    viewModel { SimpleSearchViewModel(androidApplication(), get()) }

    viewModel {
        OptionsViewModel(
            get(),
            get(),
            get(),
            get(named(KoinConstants.APP_SCOPE))
        )
    }

    viewModel {
        DownloadPickerViewModel(
            get(),
            get(),
            get(named(KoinConstants.APP_SCOPE))
        )
    }

    // Dialogs
    viewModel { AddBookmarkGroupDialogViewModel(androidApplication(), get()) }
    viewModel { BookmarkDialogViewModel(androidApplication(), get()) }
    viewModel { ChangeBookmarkGroupNameViewModel(androidApplication(), get()) }
    viewModel {
        DeleteBookmarkGroupDialogViewModel(
            androidApplication(),
            get()
        )
    }
    viewModel {
        MoveBookmarkDialogViewModel(
            get(),
            get(),
            get(named(KoinConstants.APP_SCOPE))
        )
    }
    viewModel { (source: String) ->
        GenrePickerViewModel(
            get(named(source)),
            androidApplication()
        )
    }

    viewModel { ScreenBrightnessViewModel(get()) }
}