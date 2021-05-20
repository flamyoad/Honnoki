package com.flamyoad.honnoki.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.adapter.MangaLoadStateAdapter
import com.flamyoad.honnoki.adapter.RecentMangaListAdapter
import com.flamyoad.honnoki.adapter.TrendingMangaAdapter
import com.flamyoad.honnoki.databinding.FragmentHomeListBinding
import com.flamyoad.honnoki.model.Manga
import com.flamyoad.honnoki.model.TabType
import com.flamyoad.honnoki.ui.overview.MangaOverviewActivity
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalPagingApi
class HomeListFragment : Fragment() {
    private val viewModel: HomeViewModel by activityViewModels()
    private val binding by viewLifecycleLazy { FragmentHomeListBinding.bind(requireView()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val trendingMangaAdapter = TrendingMangaAdapter(requireContext(), this::openManga)

        val recentMangaAdapter = RecentMangaListAdapter(this::openManga)

        recentMangaAdapter.withLoadStateFooter(MangaLoadStateAdapter { recentMangaAdapter.retry() })

        val concatAdapter = ConcatAdapter(trendingMangaAdapter, recentMangaAdapter)
        val layoutManager = GridLayoutManager(requireContext(), 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position == 0)
                    return 3
                else
                    return 1
            }
        }

        with(binding.listManga) {
            this.adapter = concatAdapter
            this.layoutManager = layoutManager
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
                val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                if (firstVisiblePosition != 0) {
                    viewModel.setShouldShrinkFab(true)
                } else {
                    viewModel.setShouldShrinkFab(false)
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
        val intent = Intent(requireContext(), MangaOverviewActivity::class.java).apply {
            putExtra(MangaOverviewActivity.MANGA_URL, manga.link)
            putExtra(MangaOverviewActivity.MANGA_SOURCE, manga.source.toString())
            putExtra(MangaOverviewActivity.MANGA_TITLE, manga.title)
        }
        requireContext().startActivity(intent)
    }

    companion object {
        const val TAB_GENRE = "HomeListFragment.TAB_GENRE"
        const val TAB_TYPE = "HomeListFragment.TAB_TYPE"

        @JvmStatic
        fun newInstance(tab: TabType) =
            HomeListFragment().apply {
                arguments = bundleOf(
                    TAB_GENRE to tab.genre,
                    TAB_TYPE to tab.type.readableName
                )
            }
    }
}
