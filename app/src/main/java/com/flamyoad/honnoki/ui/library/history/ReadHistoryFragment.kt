package com.flamyoad.honnoki.ui.library.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.honnoki.data.model.ReadHistory
import com.flamyoad.honnoki.data.model.State
import com.flamyoad.honnoki.databinding.FragmentReadHistoryBinding
import com.flamyoad.honnoki.ui.library.history.model.ViewReadHistory
import com.flamyoad.honnoki.ui.overview.MangaOverviewActivity
import com.flamyoad.honnoki.ui.reader.ReaderActivity
import com.flamyoad.honnoki.utils.ui.ToggleState
import com.kennyc.view.MultiStateView
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalPagingApi
class ReadHistoryFragment : Fragment() {

    private var _binding: FragmentReadHistoryBinding? = null
    val binding get() = requireNotNull(_binding)

    private val viewModel: ReadHistoryViewModel by viewModel()

    private val historyAdapter by lazy {
        ReadHistoryAdapter(
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
            viewModel.readHistory.collectLatest {
                when (it) {
                    is State.Loading -> {
                        binding.multiStateView.viewState = MultiStateView.ViewState.LOADING
                    }
                    is State.Success -> {
                        binding.multiStateView.viewState = MultiStateView.ViewState.CONTENT
                        historyAdapter.submitList(it.value)
                    }
                }
            }
        }
    }

    private fun openOverviewScreen(history: ReadHistory) {
        val intent = Intent(requireContext(), MangaOverviewActivity::class.java).apply {
            putExtra(MangaOverviewActivity.MANGA_URL, history.overviewLink)
            putExtra(MangaOverviewActivity.MANGA_SOURCE, history.source.toString())
            putExtra(MangaOverviewActivity.MANGA_TITLE, history.mainTitle)
        }
        requireContext().startActivity(intent)
    }

    private fun openReaderScreen(history: ReadHistory) {
        ReaderActivity.start(
            requireContext(),
            chapterId = history.lastReadChapterId,
            overviewId = history.overviewId,
            startAtPage = history.lastReadPageNumber
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
