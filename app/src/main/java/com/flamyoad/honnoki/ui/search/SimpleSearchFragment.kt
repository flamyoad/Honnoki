package com.flamyoad.honnoki.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.honnoki.BaseFragment

import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.adapter.MangaLoadStateAdapter
import com.flamyoad.honnoki.adapter.SimpleSearchResultAdapter
import com.flamyoad.honnoki.databinding.FragmentSimpleSearchBinding
import com.flamyoad.honnoki.model.Manga
import com.flamyoad.honnoki.model.SearchResult
import com.flamyoad.honnoki.model.Source
import com.flamyoad.honnoki.ui.overview.MangaOverviewActivity
import com.flamyoad.honnoki.ui.search.result.AdvancedSearchResultActivity
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy
import com.kennyc.view.MultiStateView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalPagingApi
class SimpleSearchFragment : BaseFragment() {
    private val viewModel: SimpleSearchViewModel by viewModels()

    private val binding by viewLifecycleLazy { FragmentSimpleSearchBinding.bind(requireView()) }

    private val searchResultAdapter = SimpleSearchResultAdapter(this::openManga)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_simple_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { return true }

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.submitQuery(query ?: "")
                return true
            }
        })
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())

        with(binding.listSearchResult) {
            adapter = searchResultAdapter
            layoutManager = linearLayoutManager
        }

        searchResultAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Error -> binding.listSearchResultView.viewState = MultiStateView.ViewState.ERROR
                is LoadState.Loading -> binding.listSearchResultView.viewState = MultiStateView.ViewState.LOADING
            }
        }

        lifecycleScope.launch {
            viewModel.searchResult.collectLatest {
                searchResultAdapter.submitData(it)
                binding.listSearchResultView.viewState = MultiStateView.ViewState.CONTENT
            }
        }
    }

    private fun openManga(searchResult: SearchResult) {
        val intent = Intent(requireContext(), MangaOverviewActivity::class.java).apply {
            putExtra(MangaOverviewActivity.MANGA_URL, searchResult.link)
            putExtra(MangaOverviewActivity.MANGA_SOURCE, Source.MANGAKALOT.toString())
            putExtra(MangaOverviewActivity.MANGA_TITLE, searchResult.title)
        }
        requireContext().startActivity(intent)
    }

    override fun getTitle(): String {
        return "Search"
    }

    companion object {
        @JvmStatic
        fun newInstance() = SimpleSearchFragment()
    }
}
