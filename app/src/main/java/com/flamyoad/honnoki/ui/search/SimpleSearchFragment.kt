package com.flamyoad.honnoki.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.honnoki.BaseFragment

import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.adapter.SimpleSearchResultAdapter
import com.flamyoad.honnoki.databinding.FragmentSimpleSearchBinding
import com.flamyoad.honnoki.data.model.SearchResult
import com.flamyoad.honnoki.data.model.Source
import com.flamyoad.honnoki.di.viewModelModules
import com.flamyoad.honnoki.ui.overview.MangaOverviewActivity
import com.flamyoad.honnoki.ui.search.model.SearchGenre
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy
import com.kennyc.view.MultiStateView
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.unloadKoinModules

@ExperimentalPagingApi
class SimpleSearchFragment : BaseFragment() {
    private val viewModel: SimpleSearchViewModel by viewModel()

    private val binding by viewLifecycleLazy { FragmentSimpleSearchBinding.bind(requireView()) }

    private val genreAdapter = GenrePickerAdapter(this::selectGenre)
    private val searchResultAdapter = SimpleSearchResultAdapter(this::openManga)
    private val searchResultEndOfListAdapter = SearchResultEndOfListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_simple_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initSearchResultList()
        initGenreList()
        observeUi()
    }

    private fun initUi() {
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { return true }

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.submitQuery(query ?: "")
                return true
            }
        })
    }

    private fun initSearchResultList() {
        val linearLayoutManager = LinearLayoutManager(requireContext())

        val concatAdapter = ConcatAdapter().apply {
            addAdapter(searchResultAdapter)
        }

        with(binding.listSearchResult) {
            adapter = concatAdapter
            layoutManager = linearLayoutManager
        }

        searchResultAdapter.addLoadStateListener {
            when (it.mediator?.refresh) {
                is LoadState.Error -> binding.listSearchResultView.viewState = MultiStateView.ViewState.ERROR
                is LoadState.Loading -> binding.listSearchResultView.viewState = MultiStateView.ViewState.LOADING
                is LoadState.NotLoading -> binding.listSearchResultView.viewState = MultiStateView.ViewState.CONTENT
            }
            when (it.mediator?.append) {
                is LoadState.NotLoading -> concatAdapter.addAdapter(searchResultEndOfListAdapter)
            }
        }
    }

    private fun initGenreList() {
//        val flexLayoutManager = FlexboxLayoutManager(requireContext()).apply {
//            flexDirection = FlexDirection.ROW
//            flexWrap = FlexWrap.WRAP
//        }

        val gridLayoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.HORIZONTAL, false)

        with(binding.selectLayout.listGenres) {
            adapter = genreAdapter
            layoutManager = gridLayoutManager
            itemAnimator = null
        }
    }

    private fun observeUi() {
        binding.listSearchResultView.viewState = MultiStateView.ViewState.CONTENT

        lifecycleScope.launchWhenResumed {
            viewModel.searchResult.collectLatest {
                searchResultAdapter.submitData(it)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.genreList().collectLatest {
                genreAdapter.submitList(it)
            }
        }
    }

    private fun selectGenre(genre: SearchGenre) {
        viewModel.selectGenre(genre)
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
