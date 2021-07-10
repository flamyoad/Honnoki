package com.flamyoad.honnoki.ui.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.*
import com.flamyoad.honnoki.databinding.FragmentVerticalScrollingReaderBinding
import com.flamyoad.honnoki.ui.reader.adapter.FailedToLoadNextChapterAdapter
import com.flamyoad.honnoki.ui.reader.adapter.ReaderImageAdapter
import com.flamyoad.honnoki.ui.reader.adapter.ReaderLoadingAdapter
import com.flamyoad.honnoki.ui.reader.model.ReaderPage
import com.flamyoad.honnoki.utils.ui.onItemsArrived
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalPagingApi
class VerticalScrollingReaderFragment : Fragment(), VolumeButtonScroller.Listener {

    private var _binding: FragmentVerticalScrollingReaderBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val parentViewModel: ReaderViewModel by sharedViewModel()
    private val viewModel: VerticalScrollingReaderViewModel by viewModel()

    private val concatAdapter = ConcatAdapter()
    private val loadingAdapter = ReaderLoadingAdapter()
    private val failedToLoadNextChapAdapter by lazy {
        FailedToLoadNextChapterAdapter(parentViewModel::loadNextChapter)
    }
    private val readerAdapter by lazy {
        ReaderImageAdapter(parentViewModel.source, parentViewModel.mangadexQualityMode)
    }

    private val linearLayoutManager by lazy { LinearLayoutManager(requireContext()) }

    private var initialScrollDone: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerticalScrollingReaderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        observeUi()
    }

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

    private fun initUi() {
        concatAdapter.addAdapter(readerAdapter)

        readerAdapter.apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            onItemsArrived {
                scrollToStartingPageNumber()
            }
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

            listImages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    syncCurrentPageAndChapter()
                    parentViewModel.currentScrollPosition =
                        linearLayoutManager.findFirstVisibleItemPosition()
                    parentViewModel.setSideKickVisibility(false)

                    // Prefetch when scrolled to the last item
                    val reachedEndOfList =
                        linearLayoutManager.findLastVisibleItemPosition() >= readerAdapter.itemCount - 1
                    if (reachedEndOfList) {
                        parentViewModel.loadNextChapter()
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    val lastVisiblePos = linearLayoutManager.findFirstVisibleItemPosition()
                    val lastVisibleView = linearLayoutManager.findViewByPosition(lastVisiblePos)
                    val reachedTopOfList = lastVisiblePos == 0 && lastVisibleView?.top == 0

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
            parentViewModel.pageList().collectLatest {
                readerAdapter.submitList(it)
            }
        }

        // Set the page number in the bottom-right tooltip
        lifecycleScope.launchWhenResumed {
            parentViewModel.pageNumberScrolledBySeekbar().collectLatest {
                val currentItemScrolled = readerAdapter.currentList.getOrNull(it - 1) ?: return@collectLatest
                if (currentItemScrolled is ReaderPage.Value) {
                    parentViewModel.setCurrentPageNumber(currentItemScrolled.page.number)
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            parentViewModel.pageNumberScrolledBySeekbar()
                .collectLatest { pageNumberScrolledBySeekbar ->
                    val currentChapter = parentViewModel.currentChapter
                    val adapterItems = readerAdapter.currentList

                    val pagePositionInList = withContext(Dispatchers.Default) {
                        adapterItems.indexOfFirst {
                            if (it is ReaderPage.Value)
                                it.chapter == currentChapter && it.page.number == pageNumberScrolledBySeekbar
                            else {
                                false
                            }
                        }
                    }

                    withContext(Dispatchers.Main) {
                        linearLayoutManager.scrollToPositionWithOffset(pagePositionInList, 0)
                    }
                }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.disablePullToRefresh.collectLatest {
                binding.smartRefreshLayout.isEnabled = it
            }
        }

        lifecycleScope.launchWhenResumed {
            parentViewModel.showBottomLoadingIndicator().collectLatest {
                when (it) {
                    true -> concatAdapter.addAdapter(loadingAdapter)
                    false -> concatAdapter.removeAdapter(loadingAdapter)
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            parentViewModel.failedToLoadNextChapter().collectLatest {
                when (it) {
                    true -> concatAdapter.addAdapter(failedToLoadNextChapAdapter)
                    false -> concatAdapter.removeAdapter(failedToLoadNextChapAdapter)
                }
            }
        }
    }

    private fun syncCurrentPageAndChapter() {
        val currentPage =
            readerAdapter.currentList.getOrNull(linearLayoutManager.findFirstVisibleItemPosition())
        currentPage?.let {
            if (it is ReaderPage.Value) {
                parentViewModel.setCurrentChapter(it.chapter)
                parentViewModel.setCurrentPageNumber(it.page.number)
            }
        }
    }

    private fun scrollToStartingPageNumber() {
        if (initialScrollDone) return

        val startingPageNumber = requireActivity().intent.getIntExtra(ReaderActivity.START_AT_PAGE, 0)
        linearLayoutManager.scrollToPositionWithOffset(startingPageNumber - 1, 0)

        initialScrollDone = true
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

        private const val INITIAL_SCROLL_DONE = "initial_scroll_done"

        @JvmStatic
        fun newInstance() = VerticalScrollingReaderFragment()
    }
}

