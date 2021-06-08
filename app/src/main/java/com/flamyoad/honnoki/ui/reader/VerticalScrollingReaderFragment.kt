package com.flamyoad.honnoki.ui.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.ui.reader.adapter.ReaderImageAdapter
import com.flamyoad.honnoki.ui.reader.adapter.ReaderLoadingAdapter
import com.flamyoad.honnoki.databinding.FragmentVerticalScrollingReaderBinding
import com.flamyoad.honnoki.ui.reader.adapter.FailedToLoadNextChapterAdapter
import com.flamyoad.honnoki.ui.reader.model.LoadType
import com.flamyoad.honnoki.ui.reader.model.ReaderPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@ExperimentalPagingApi
class VerticalScrollingReaderFragment : Fragment() {

    private var _binding: FragmentVerticalScrollingReaderBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val parentViewModel: ReaderViewModel by sharedViewModel()
    private val viewModel: VerticalScrollingReaderViewModel by viewModel()

    private val concatAdapter = ConcatAdapter()
    private val readerAdapter = ReaderImageAdapter()
    private val loadingAdapter = ReaderLoadingAdapter()
    private val failedToLoadNextChapAdapter by lazy {
        FailedToLoadNextChapterAdapter(parentViewModel::loadNextChapter)
    }

    private val linearLayoutManager by lazy { LinearLayoutManager(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerticalScrollingReaderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            val chapterId =
                requireActivity().intent?.getLongExtra(ReaderActivity.CHAPTER_ID, -1) ?: -1
            parentViewModel.fetchChapterImages(chapterId, LoadType.INITIAL)
        }

        initUi()
        observeUi()
    }

    private fun initUi() {
        parentViewModel.setSideKickVisibility(false)

        readerAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        concatAdapter.addAdapter(readerAdapter)

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

                    // Prefetch when scrolled to the second last item (minus ads & last page)
                    val reachedEndOfList =
                        linearLayoutManager.findLastVisibleItemPosition() >= readerAdapter.itemCount - 2
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

        lifecycleScope.launchWhenResumed {
            parentViewModel.pageNumberScrolledBySeekbar().collectLatest {
                val currentItemScrolled =
                    readerAdapter.currentList.getOrNull(it) ?: return@collectLatest
                if (currentItemScrolled is ReaderPage.Value) {
                    // Set the page number in the bottom-right tooltip
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = VerticalScrollingReaderFragment()
    }
}
