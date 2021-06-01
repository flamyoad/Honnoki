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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.adapter.ReaderImageAdapter
import com.flamyoad.honnoki.databinding.FragmentReaderFrameBinding
import com.flamyoad.honnoki.model.Chapter
import com.flamyoad.honnoki.model.State
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

    private var hasLoadedData: Boolean = false

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
        if (hasLoadedData) return

        val chapterUrl = arguments?.getString(CHAPTER_URL) ?: ""
        viewModel.fetchManga(chapterUrl)

        initUi()
        observeUi()

        hasLoadedData = true
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
            })
        }
    }

    private fun observeUi() {
        viewModel.pageList().observe(viewLifecycleOwner) {
            when (it) {
                is State.Success -> {
                    readerAdapter.submitList(it.value)
                    parentViewModel.setTotalPages(it.value.size)
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            parentViewModel.pageNumberScrolledBySeekbar().collectLatest {
                binding.listImages.scrollToPosition(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        hasLoadedData = false
    }

    companion object {
        const val CHAPTER_ID = "chapter_id"
        const val CHAPTER_NAME = "chapter_name"
        const val CHAPTER_URL = "chapter_url"

        @JvmStatic
        fun newInstance(chapter: Chapter) = ReaderFrameFragment().apply {
            arguments = Bundle().apply {
                putString(CHAPTER_URL, chapter.link)
            }
        }
    }
}
