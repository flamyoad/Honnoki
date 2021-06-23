package com.flamyoad.honnoki.ui.home.mangalist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.ui.home.adapter.MangaLoadStateAdapter
import com.flamyoad.honnoki.ui.home.adapter.VerticalMangaHeaderAdapter
import com.flamyoad.honnoki.ui.home.adapter.VerticalMangaListAdapter
import com.flamyoad.honnoki.ui.home.adapter.HorizontalMangaAdapter
import com.flamyoad.honnoki.databinding.FragmentDetailedMangaListBinding
import com.flamyoad.honnoki.data.entities.Manga
import com.flamyoad.honnoki.data.Source
import com.flamyoad.honnoki.data.entities.SearchResult
import com.flamyoad.honnoki.ui.home.HomeViewModel
import com.flamyoad.honnoki.ui.overview.MangaOverviewActivity
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val GRID_SPANCOUNT = 3

@ExperimentalPagingApi
class DetailedMangaListFragment : Fragment() {
    private val binding by viewLifecycleLazy { FragmentDetailedMangaListBinding.bind(requireView()) }

    private val parentViewModel: HomeViewModel by sharedViewModel()

    private val viewModel: HomeListViewModel by viewModel {
        val sourceName = arguments?.getString(SOURCE) ?: ""
        parametersOf(sourceName)
    }

    private val gridLayoutManager by lazy { GridLayoutManager(requireContext(), GRID_SPANCOUNT) }

    override fun onResume() {
        super.onResume()
        if (gridLayoutManager.findFirstVisibleItemPosition() == 0) {
            parentViewModel.setShouldShrinkFab(false)
        } else {
            parentViewModel.setShouldShrinkFab(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detailed_manga_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCROLL_POSITION, gridLayoutManager.findFirstVisibleItemPosition())
    }

    private fun initRecyclerView() {
        val trendingMangaAdapter = HorizontalMangaAdapter(requireContext(), this::openManga)
        trendingMangaAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        val recentMangaHeaderAdapter = VerticalMangaHeaderAdapter({})

        val recentMangaAdapter = VerticalMangaListAdapter(this::openManga).apply {
            withLoadStateFooter(MangaLoadStateAdapter { this.retry() })
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        val concatAdapter =
            ConcatAdapter(trendingMangaAdapter, recentMangaHeaderAdapter, recentMangaAdapter)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (position) {
                    0 -> GRID_SPANCOUNT
                    1 -> GRID_SPANCOUNT
                    else -> 1
                }
            }
        }

        with(binding.listManga) {
            this.adapter = concatAdapter
            this.layoutManager = gridLayoutManager
            itemAnimator = null
        }

        lifecycleScope.launch {
            viewModel.getTrendingManga().collectLatest {
                trendingMangaAdapter.submitDataToChild(it)
            }
        }

        lifecycleScope.launch {
            viewModel.getRecentManga().collectLatest {
                recentMangaAdapter.submitData(it)
            }
        }

        // Listener to determine whether to shrink or expand FAB (Shrink after 1st item in list is no longer visible)
        binding.listManga.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisiblePosition = gridLayoutManager.findFirstVisibleItemPosition()
                if (firstVisiblePosition != 0) {
                    parentViewModel.setShouldShrinkFab(true)
                } else {
                    parentViewModel.setShouldShrinkFab(false)
                }
            }
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            trendingMangaAdapter.refresh()
            recentMangaAdapter.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun openManga(manga: Manga) {
        MangaOverviewActivity.startActivity(
            context = requireContext(),
            mangaUrl = manga.link,
            mangaSource = manga.source,
            mangaTitle = manga.title
        )
    }

    companion object {
        private const val SCROLL_POSITION = "scroll_position"
        private const val SOURCE = "source"

        @JvmStatic
        fun newInstance(source: Source) = DetailedMangaListFragment().apply {
            arguments = Bundle().apply {
                putString(SOURCE, source.toString())
            }
        }
    }
}
