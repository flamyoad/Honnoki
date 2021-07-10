package com.flamyoad.honnoki.ui.library.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.honnoki.cache.CoverCache
import com.flamyoad.honnoki.data.entities.ReadHistory
import com.flamyoad.honnoki.databinding.FragmentReadHistoryBinding
import com.flamyoad.honnoki.ui.overview.MangaOverviewActivity
import com.flamyoad.honnoki.ui.reader.ReaderActivity
import com.kennyc.view.MultiStateView
import kotlinx.coroutines.flow.collectLatest
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalPagingApi
class ReadHistoryFragment : Fragment() {

    private var _binding: FragmentReadHistoryBinding? = null
    val binding get() = requireNotNull(_binding)

    private val viewModel: ReadHistoryViewModel by viewModel()

    private val coverCache: CoverCache by inject()

    private val historyAdapter by lazy {
        ReadHistoryAdapter(
            coverCache,
            this::openOverviewScreen,
            this::openReaderScreen,
            this::removeItem
        )
    }
    private val linearLayoutManager by lazy { LinearLayoutManager(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentReadHistoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.listReadHistory) {
            adapter = historyAdapter
            layoutManager = linearLayoutManager
        }

        lifecycleScope.launchWhenResumed {
            viewModel.readHistory
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collectLatest {
                    binding.multiStateView.viewState = MultiStateView.ViewState.CONTENT
                    historyAdapter.submitData(it)
                }
        }
    }

    private fun openOverviewScreen(history: ReadHistory) {
        MangaOverviewActivity.startActivity(
            context = requireContext(),
            mangaUrl = history.overview.link,
            mangaSource = history.overview.source!!,
            mangaTitle = history.overview.mainTitle
        )
    }

    private fun openReaderScreen(history: ReadHistory) {
        ReaderActivity.start(
            requireContext(),
            chapterId = history.overview.lastReadChapterId,
            overviewId = history.overview.id!!,
            startAtPage = history.overview.lastReadPageNumber,
            source = history.overview.source!!
        )
    }

    private fun removeItem(history: ReadHistory) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ReadHistoryFragment()
    }
}
