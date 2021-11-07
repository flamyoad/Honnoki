package com.flamyoad.honnoki.ui.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.*
import com.flamyoad.honnoki.data.entities.Chapter
import com.flamyoad.honnoki.databinding.FragmentVerticalScrollingReaderBinding
import com.flamyoad.honnoki.ui.reader.adapter.FailedToLoadNextChapterAdapter
import com.flamyoad.honnoki.ui.reader.adapter.ReaderImageAdapter
import com.flamyoad.honnoki.ui.reader.adapter.ReaderLoadingAdapter
import com.flamyoad.honnoki.ui.reader.model.ReaderPage
import com.flamyoad.honnoki.utils.ui.onItemsArrived
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalPagingApi
class VerticalScrollingReaderFragment : BaseReaderFragment() {

    private var _binding: FragmentVerticalScrollingReaderBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: VerticalScrollingReaderViewModel by viewModel()

    private val concatAdapter = ConcatAdapter()
    private val loadingAdapter = ReaderLoadingAdapter()
    private val failedToLoadNextChapAdapter by lazy {
        FailedToLoadNextChapterAdapter(parentViewModel::loadNextChapter)
    }
    private val readerAdapter by lazy {
        ReaderImageAdapter(
            parentViewModel.source,
            parentViewModel.mangadexQualityMode
        )
    }

    private val linearLayoutManager by lazy { LinearLayoutManager(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerticalScrollingReaderBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        observeUi()
    }

    private fun initUi() {
        concatAdapter.addAdapter(readerAdapter)

        readerAdapter.apply {
            stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            onItemsArrived { initializeReader() }
        }

        with(binding) {
            with(listImages) {
                adapter = concatAdapter
                layoutManager = linearLayoutManager
                addItemDecoration(
                    DividerItemDecoration(
                        requireContext(),
                        DividerItemDecoration.VERTICAL
                    )
                )
            }

            listImages.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    super.onScrolled(recyclerView, dx, dy)

                    val firstVisibleItemPosition =
                        linearLayoutManager.findFirstVisibleItemPosition()
                    val currentPage =
                        readerAdapter.currentList.getOrNull(
                            firstVisibleItemPosition
                        )
                    if (currentPage != null) {
                        onPageScroll(
                            currentPage,
                            firstVisibleItemPosition
                        )
                    }
                    // Prefetch when scrolled to the last item
                    val reachedEndOfList =
                        firstVisibleItemPosition >= readerAdapter.itemCount - 1
                    if (reachedEndOfList) {
                        parentViewModel.loadNextChapter()
                    }
                }

                override fun onScrollStateChanged(
                    recyclerView: RecyclerView,
                    newState: Int
                ) {
                    super.onScrollStateChanged(recyclerView, newState)

                    val lastVisiblePos =
                        linearLayoutManager.findFirstVisibleItemPosition()
                    val lastVisibleView =
                        linearLayoutManager.findViewByPosition(lastVisiblePos)
                    val reachedTopOfList =
                        lastVisiblePos == 0 && lastVisibleView?.top == 0

                    if (reachedTopOfList) {
                        viewModel.setPullToRefreshEnabled(true)
                    } else {
                        viewModel.setPullToRefreshEnabled(false)
                    }
                }
            })

            smartRefreshLayout.setOnRefreshListener {
                parentViewModel.loadPreviousChapter()
                it.finishRefresh(true)
            }
        }
    }

    private fun observeUi() {
        lifecycleScope.launchWhenResumed {
            viewModel.disablePullToRefresh.collectLatest {
                binding.smartRefreshLayout.isEnabled = it
            }
        }
    }

    override fun scrollTo(zeroIndexed: Int) {
        var list = readerAdapter.currentList
        println("list:" + list)
        println("zeroIndexed" + zeroIndexed)
        linearLayoutManager.scrollToPositionWithOffset(
            zeroIndexed,
            0
        )
    }

    override fun onPagesLoaded(pages: List<ReaderPage>) {
        readerAdapter.submitList(pages)
    }

    override fun onLoadingNextChapter(isLoading: Boolean) {
        when (isLoading) {
            true -> concatAdapter.addAdapter(loadingAdapter)
            false -> concatAdapter.removeAdapter(loadingAdapter)
        }
    }

    override fun onFailedToLoadNextChapter(hasFailed: Boolean) {
        when (hasFailed) {
            true -> concatAdapter.addAdapter(failedToLoadNextChapAdapter)
            false -> concatAdapter.removeAdapter(failedToLoadNextChapAdapter)
        }
    }

    override fun onSeekbarPositionChanged(
        position: Int,
        currentChapter: Chapter
    ) {
        val adapterItems = readerAdapter.currentList

        val pagePositionInList =
            adapterItems.indexOfFirst {
                if (it is ReaderPage.Value)
                    it.chapter == currentChapter && it.page.number == position
                else {
                    false
                }
            }

        linearLayoutManager.scrollToPositionWithOffset(
            pagePositionInList,
            0
        )
    }

    private fun scrollToStartingPageNumber() {
//        if (initialScrollDone) return
//
//        lifecycleScope.launch {
//            val overviewId = requireActivity().intent.getLongExtra(
//                ReaderActivity.OVERVIEW_ID,
//                -1
//            )
//            val currentChapterId = requireActivity().intent.getLongExtra(
//                ReaderActivity.CHAPTER_ID,
//                -1
//            )
//            val overview =
//                viewModel.getMangaOverview(overviewId) ?: return@launch
//
//            if (currentChapterId == overview.lastReadChapterId) {
//                linearLayoutManager.scrollToPositionWithOffset(
//                    overview.lastReadPageNumber - 1,
//                    0
//                )
//            }
//        }
//
//        initialScrollDone = true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onNextPage() {
        val position = linearLayoutManager.findLastVisibleItemPosition()
        binding.listImages.scrollToPosition(position + 1)
    }

    override fun onPrevPage() {
        val position = linearLayoutManager.findFirstVisibleItemPosition()
        if (position <= 0) return
        binding.listImages.scrollToPosition(position - 1)
    }

    override fun scrollByFixedDistance(distance: Int) {
        TODO("Not yet implemented")
    }

    companion object {
        const val TAG = "vertical_scrolling_reader_fragment"

        @JvmStatic
        fun newInstance() = VerticalScrollingReaderFragment()
    }
}

