package com.flamyoad.honnoki.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.honnoki.BaseFragment

import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.data.GenreConstants
import com.flamyoad.honnoki.ui.search.adapter.SimpleSearchResultAdapter
import com.flamyoad.honnoki.databinding.FragmentSimpleSearchBinding
import com.flamyoad.honnoki.data.entities.SearchResult
import com.flamyoad.honnoki.data.Source
import com.flamyoad.honnoki.ui.overview.MangaOverviewActivity
import com.flamyoad.honnoki.ui.search.adapter.GenrePickerAdapter
import com.flamyoad.honnoki.ui.search.adapter.SearchResultEndOfListAdapter
import com.flamyoad.honnoki.ui.search.adapter.SourcePickerAdapter
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.kennyc.view.MultiStateView
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalPagingApi
class SimpleSearchFragment : BaseFragment() {
    private val viewModel: SimpleSearchViewModel by viewModel()

    private val binding by viewLifecycleLazy { FragmentSimpleSearchBinding.bind(requireView()) }

    private val genreAdapter by lazy { GenrePickerAdapter(viewModel::selectGenre) }
    private val sourceAdapter by lazy { SourcePickerAdapter(viewModel::selectSource) }
    private val searchResultAdapter = SimpleSearchResultAdapter(this::openManga)
    private val searchResultEndOfListAdapter = SearchResultEndOfListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_simple_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initSearchResultList()
        initGenreList()
        initSourceList()

        observeUi()

        savedInstanceState?.let { bundle ->
            bundle.getParcelable<Source>(SELECTED_SOURCE)?.let { src ->
                viewModel.selectSource(src)
            }
            bundle.getParcelable<GenreConstants>(SELECTED_GENRE)?.let { genre ->
                viewModel.selectGenre(genre)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(SELECTED_SOURCE, viewModel.selectedSource().value)
        outState.putParcelable(SELECTED_GENRE, viewModel.selectedGenre().value)
    }

    private fun initUi() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.submitQuery(query ?: "")
                return true
            }
        })

        with(binding.selectLayout) {
            cardViewSourceSelector.setOnClickListener {
                listSource.isVisible = !listSource.isVisible
                txtSource.isVisible = !txtSource.isVisible
            }
        }
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
                is LoadState.Error -> binding.listSearchResultView.viewState =
                    MultiStateView.ViewState.ERROR
                is LoadState.Loading -> binding.listSearchResultView.viewState =
                    MultiStateView.ViewState.LOADING
                is LoadState.NotLoading -> binding.listSearchResultView.viewState =
                    MultiStateView.ViewState.CONTENT
            }
            when (it.mediator?.append) {
                is LoadState.NotLoading -> concatAdapter.addAdapter(searchResultEndOfListAdapter)
            }
        }
    }

    private fun initGenreList() {
        val gridLayoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.HORIZONTAL, false)

        with(binding.selectLayout.listGenres) {
            adapter = genreAdapter
            layoutManager = gridLayoutManager
        }
    }

    private fun initSourceList() {
        val flexLayoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }

        with(binding.selectLayout.listSource) {
            adapter = sourceAdapter
            layoutManager = flexLayoutManager
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

        lifecycleScope.launchWhenResumed {
            viewModel.sourceList().collectLatest {
                sourceAdapter.submitList(it)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.selectedSource().collectLatest {
                binding.selectLayout.txtSource.text = it.title
            }
        }
    }

    private fun openManga(searchResult: SearchResult) {
        val intent = Intent(requireContext(), MangaOverviewActivity::class.java).apply {
            putExtra(MangaOverviewActivity.MANGA_URL, searchResult.link)
            putExtra(MangaOverviewActivity.MANGA_SOURCE, viewModel.selectedSource().value.toString())
            putExtra(MangaOverviewActivity.MANGA_TITLE, searchResult.title)
        }
        requireContext().startActivity(intent)
    }

    override val bottomBarTitle: String
        get() = "Search"

    companion object {
        private const val SELECTED_GENRE = "selected_genre"
        private const val SELECTED_SOURCE = "selected_source"

        @JvmStatic
        fun newInstance() = SimpleSearchFragment()
    }
}
