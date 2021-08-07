package com.flamyoad.honnoki.ui.home.mangalist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.data.entities.Manga
import com.flamyoad.honnoki.databinding.FragmentDetailedMangaListBinding
import com.flamyoad.honnoki.databinding.FragmentSimpleMangaListBinding
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.source.model.TabType
import com.flamyoad.honnoki.ui.home.HomeViewModel
import com.flamyoad.honnoki.ui.home.adapter.*
import com.flamyoad.honnoki.ui.overview.MangaOverviewActivity
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy
import com.flamyoad.honnoki.utils.ui.onItemsArrived
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val GRID_SPANCOUNT = 3

@ExperimentalPagingApi
class DetailedMangaListFragment : Fragment() {
    private var _binding: FragmentDetailedMangaListBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val parentViewModel: HomeViewModel by sharedViewModel()

    private val viewModel: MangaListViewModel by viewModel {
        val sourceName = arguments?.getString(SOURCE) ?: ""
        parametersOf(sourceName)
    }

    private lateinit var concatAdapter: ConcatAdapter

    private val trendingMangaLoadingAdapter by lazy { LoadIndicatorAdapter() }
    private val listDividerAdapter by lazy { ListDividerAdapter() }
    private val recentMangaLoadingAdapter by lazy { LoadIndicatorAdapter() }
    private val recentMangaHeaderAdapter by lazy { VerticalMangaHeaderAdapter() }

    private lateinit var trendingMangaAdapter: HorizontalMangaAdapter
    private lateinit var recentMangaAdapter: VerticalMangaListAdapter

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
    ): View {
        _binding = FragmentDetailedMangaListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTrendingList()
        initRecentList()
        initConcatList()
        initUi()
        observeUi()
    }

    private fun initUi() {
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

    private fun observeUi() {
        // WHAT IS HAPPENING?!!!!
        // Do not try to inline these 2 variables - recentMangas, trendingMangas
        // Otherwise, the page keep refreshes when you swipe back from another page to this page

        // It works now but Try to figure this out?!!
        // It maybe caused by fact that Flow is cold stream?!
        val recentMangas = viewModel.getRecentManga()
        val trendingMangas = viewModel.getTrendingManga()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    trendingMangas.collectLatest {
                        trendingMangaAdapter.submitDataToChild(it)
                    }
                }
                launch {
                    recentMangas.collectLatest {
                        recentMangaAdapter.submitData(it)
                    }
                }
            }
        }
    }

    private fun initConcatList() {
        concatAdapter = ConcatAdapter(
            trendingMangaAdapter,
            trendingMangaLoadingAdapter,
            listDividerAdapter,
            recentMangaHeaderAdapter,
            recentMangaLoadingAdapter,
            recentMangaAdapter
        )

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val adapterList = concatAdapter.adapters
                // Position will go up to 100~ depending on the item of VerticalMangaListAdapter
                if (position >= adapterList.size) {
                    return 1
                }

                return when (adapterList[position]) {
                    is HorizontalMangaAdapter -> GRID_SPANCOUNT
                    is VerticalMangaHeaderAdapter -> GRID_SPANCOUNT
                    is VerticalMangaListAdapter -> 1
                    is LoadIndicatorAdapter -> GRID_SPANCOUNT
                    is ListDividerAdapter -> GRID_SPANCOUNT
                    else -> throw IllegalArgumentException("Unknown adapter")
                }
            }
        }

        with(binding.listManga) {
            adapter = concatAdapter
            layoutManager = gridLayoutManager
            itemAnimator = null
        }
    }

    private fun initTrendingList() {
        trendingMangaAdapter = HorizontalMangaAdapter(
            requireContext(),
            this::openManga,
            onItemsLoaded = { concatAdapter.removeAdapter(trendingMangaLoadingAdapter) }
        )

        trendingMangaAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        trendingMangaAdapter.onItemsArrived {
            concatAdapter.removeAdapter(trendingMangaLoadingAdapter)
        }
    }

    private fun initRecentList() {
        recentMangaAdapter = VerticalMangaListAdapter(this::openManga)

        recentMangaAdapter.onItemsArrived {
            concatAdapter.removeAdapter(recentMangaLoadingAdapter)
        }

        recentMangaAdapter.apply {
            withLoadStateFooter(MangaLoadStateAdapter { this.retry() })
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
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
        private const val SOURCE = "source"

        @JvmStatic
        fun newInstance(source: Source) = DetailedMangaListFragment().apply {
            arguments = Bundle().apply {
                putString(SOURCE, source.toString())
            }
        }
    }
}

