package com.flamyoad.honnoki.ui.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.adapter.ReaderImageAdapter
import com.flamyoad.honnoki.adapter.ReaderLoadingAdapter
import com.flamyoad.honnoki.databinding.FragmentReaderFrameBinding
import com.flamyoad.honnoki.ui.reader.model.LoadType
import com.flamyoad.honnoki.ui.reader.model.ReaderPage
import kotlinx.coroutines.flow.collectLatest

@ExperimentalPagingApi
class ReaderFrameFragment : Fragment() {

    private var _binding: FragmentReaderFrameBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val parentViewModel: ReaderViewModel by activityViewModels()
    private val viewModel: ReaderFrameViewModel by viewModels()

    private val concatAdapter = ConcatAdapter()
    private val readerAdapter = ReaderImageAdapter()
    private val loadingAdapter = ReaderLoadingAdapter()
    private val linearLayoutManager by lazy { LinearLayoutManager(requireContext()) }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentReaderFrameBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            val chapterId = requireActivity().intent?.getLongExtra(ReaderActivity.CHAPTER_ID, -1) ?: -1
            parentViewModel.fetchManga(chapterId, LoadType.INITIAL)
        }

        initUi()
        observeUi()
    }

    private fun initUi() {
        parentViewModel.setSideKickVisibility(false)

        readerAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
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
                    parentViewModel.currentScrollPosition = linearLayoutManager.findFirstVisibleItemPosition()
                    parentViewModel.setSideKickVisibility(false)
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    // Prefetch when scrolled to the second last item (minus ads & last page)
                    val reachedEndOfList = linearLayoutManager.findLastVisibleItemPosition() >= readerAdapter.itemCount - 2

                    if (reachedEndOfList) {
                        parentViewModel.loadNextChapter()
                    }

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
                val currentItemScrolled = readerAdapter.currentList.getOrNull(it) ?: return@collectLatest
                if (currentItemScrolled is ReaderPage.Value) {
                    parentViewModel.setCurrentPageNumber(currentItemScrolled.page.number)
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            parentViewModel.pageNumberScrolledBySeekbar().collectLatest { pageNumberScrolledBySeekbar ->
                val currentChapter = parentViewModel.currentChapter
                val adapterItems = readerAdapter.currentList

                val pagePositionInList = adapterItems
                    .filterIsInstance<ReaderPage.Value>()
                    .indexOfFirst { it.chapter == currentChapter && it.page.number == pageNumberScrolledBySeekbar }

                linearLayoutManager.scrollToPositionWithOffset(pagePositionInList, 0)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.disablePullToRefresh.collectLatest {
                binding.smartRefreshLayout.isEnabled = it
            }
        }

        lifecycleScope.launchWhenResumed {
            parentViewModel.showBottomLoadingIndicator().collectLatest {
                if (it) {
                    concatAdapter.addAdapter(loadingAdapter)
                } else {
                    concatAdapter.removeAdapter(loadingAdapter)
                }
            }
        }
    }

    private fun syncCurrentPageAndChapter() {
        val currentPage = readerAdapter.currentList.getOrNull(linearLayoutManager.findFirstVisibleItemPosition())
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
        fun newInstance() = ReaderFrameFragment()
    }
}
