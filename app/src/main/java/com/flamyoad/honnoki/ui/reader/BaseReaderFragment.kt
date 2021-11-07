package com.flamyoad.honnoki.ui.reader

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.data.entities.Chapter
import com.flamyoad.honnoki.ui.reader.model.ReaderPage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@ExperimentalPagingApi
abstract class BaseReaderFragment : Fragment(), VolumeButtonScroller.Listener {

    val parentViewModel: ReaderViewModel by sharedViewModel()

    var initialScrollDone: Boolean = false

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(INITIAL_SCROLL_DONE, initialScrollDone)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            initialScrollDone = it.getBoolean(INITIAL_SCROLL_DONE)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenResumed {
            parentViewModel.pageList().collectLatest {
                onPagesLoaded(it)
            }
        }

        lifecycleScope.launchWhenResumed {
            parentViewModel.showBottomLoadingIndicator().collectLatest {
                onLoadingNextChapter(it)
            }
        }

        lifecycleScope.launchWhenResumed {
            parentViewModel.failedToLoadNextChapter().collectLatest {
                onFailedToLoadNextChapter(it)
            }
        }

        lifecycleScope.launchWhenResumed {
            parentViewModel.pageNumberScrolledBySeekbar()
                .collectLatest {
                    onSeekbarPositionChanged(it, parentViewModel.currentChapter)
                }
        }

        // Set the page number in the bottom-right tooltip
        lifecycleScope.launchWhenResumed {
            parentViewModel.pageNumberScrolledBySeekbar().collectLatest {
                val currentItemScrolled =
                    parentViewModel.pageList().value.getOrNull(it - 1)
                        ?: return@collectLatest
                if (currentItemScrolled is ReaderPage.Value) {
                    parentViewModel.setCurrentPageNumber(currentItemScrolled.page.number)
                }
            }
        }
    }

    fun initializeReader() {
        if (initialScrollDone) return

        lifecycleScope.launch {
            val overviewId = requireActivity().intent.getLongExtra(
                ReaderActivity.OVERVIEW_ID,
                -1
            )
            val currentChapterId = requireActivity().intent.getLongExtra(
                ReaderActivity.CHAPTER_ID,
                -1
            )
            val overview =
                parentViewModel.getMangaOverview(overviewId) ?: return@launch

            if (currentChapterId == overview.lastReadChapterId) {
                scrollTo(overview.lastReadPageNumber - 1)
            }
        }

        initialScrollDone = true
    }

    fun onPageScroll(currentPage: ReaderPage, firstVisibleItemPosition: Int) {
        currentPage.let {
            if (it is ReaderPage.Value) {
                parentViewModel.setCurrentChapter(it.chapter)
                parentViewModel.setCurrentPageNumber(it.page.number)
            }
        }
        parentViewModel.currentScrollPosition = firstVisibleItemPosition
        parentViewModel.setSideKickVisibility(false)
    }

    abstract fun scrollTo(zeroIndexed: Int)

    abstract fun onPagesLoaded(pages: List<ReaderPage>)

    abstract fun onLoadingNextChapter(isLoading: Boolean)

    abstract fun onFailedToLoadNextChapter(hasFailed: Boolean)

    abstract fun onSeekbarPositionChanged(
        position: Int,
        currentChapter: Chapter
    )

    abstract override fun onNextPage()

    abstract override fun onPrevPage()

    abstract override fun scrollByFixedDistance(distance: Int)

    companion object {
        private const val INITIAL_SCROLL_DONE = "initial_scroll_done"
    }
}