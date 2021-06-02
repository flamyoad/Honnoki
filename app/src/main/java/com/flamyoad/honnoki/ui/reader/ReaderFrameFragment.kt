package com.flamyoad.honnoki.ui.reader

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.adapter.ReaderImageAdapter
import com.flamyoad.honnoki.databinding.FragmentReaderFrameBinding
import com.flamyoad.honnoki.model.Chapter
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import kotlinx.coroutines.flow.collectLatest

@ExperimentalPagingApi
class ReaderFrameFragment : Fragment() {

    private var _binding: FragmentReaderFrameBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val parentViewModel: ReaderViewModel by activityViewModels()
    private val viewModel: ReaderFrameViewModel by viewModels()

    private lateinit var readerAdapter: ReaderImageAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var scrollingFromSeekbar: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentReaderFrameBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val chapterId = requireActivity().intent?.getLongExtra(ReaderActivity.CHAPTER_ID, -1) ?: -1

        parentViewModel.fetchManga(chapterId)

        initUi()
        observeUi()
    }

    private fun initUi() {
        parentViewModel.setSideKickVisibility(false)

        with(binding) {
            readerAdapter = ReaderImageAdapter()
            linearLayoutManager = LinearLayoutManager(requireContext())

            with(listImages) {
                adapter = readerAdapter
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
                    parentViewModel.setCurrentPage(linearLayoutManager.findFirstVisibleItemPosition())

                    // Show the bottom bar when the scrolling is done by seekbar
                    if (!scrollingFromSeekbar) {
                        parentViewModel.setSideKickVisibility(false)
                    }
                    // Resets the boolean
                    scrollingFromSeekbar = false
                }

                // Prefetch new chapter when arriving end of list
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (linearLayoutManager.findLastVisibleItemPosition() == readerAdapter.itemCount - 1) {
                        parentViewModel.loadNextChapter()
                    }

                    val lastVisiblePos = linearLayoutManager.findFirstVisibleItemPosition()
                    val lastVisibleView = linearLayoutManager.findViewByPosition(lastVisiblePos)
                    if (lastVisiblePos == 0 && lastVisibleView?.top == 0) {
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
        parentViewModel.pageList().observe(viewLifecycleOwner) {
            readerAdapter.submitList(it)
            parentViewModel.setTotalPages(it.size)
        }

        lifecycleScope.launchWhenResumed {
            parentViewModel.pageNumberScrolledBySeekbar().collectLatest {
                binding.listImages.scrollToPosition(it)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.disablePullToRefresh().collectLatest {
                binding.smartRefreshLayout.isEnabled = it
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
