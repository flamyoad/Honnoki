package com.flamyoad.honnoki.ui.home.mangalist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.data.entities.Manga
import com.flamyoad.honnoki.databinding.FragmentSimpleMangaListBinding
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.source.model.TabType
import com.flamyoad.honnoki.ui.home.HomeViewModel
import com.flamyoad.honnoki.ui.home.adapter.*
import com.flamyoad.honnoki.ui.overview.MangaOverviewActivity
import com.flamyoad.honnoki.utils.extensions.getInteger
import com.flamyoad.honnoki.utils.ui.onItemsArrived
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SimpleMangaListFragment : Fragment() {
    private var _binding: FragmentSimpleMangaListBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val parentViewModel: HomeViewModel by sharedViewModel()

    private val viewModel: MangaListViewModel by viewModel {
        val sourceName = arguments?.getString(SOURCE) ?: ""
        parametersOf(sourceName)
    }

    private val gridSpanCount by lazy {
        getInteger(R.integer.manga_grid_spancount)
    }

    private val mangaAdapter by lazy { VerticalMangaListAdapter(this::openManga) }
    private val initialLoadIndicatorAdapter by lazy { LoadIndicatorAdapter() }
    private val gridLayoutManager by lazy {
        GridLayoutManager(
            requireContext(),
            gridSpanCount
        )
    }

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
        // Inflate the layout for this fragment
        _binding = FragmentSimpleMangaListBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMangaList()
        initUi()
        observeUi()
    }

    private fun initMangaList() {
        mangaAdapter.apply {
            stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        val loadingFooter = MangaLoadStateAdapter(
            retry = { mangaAdapter.retry() }
        )

        val concatAdapter = mangaAdapter.withLoadStateFooter(loadingFooter)

        gridLayoutManager.spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val mangaCount = mangaAdapter.itemCount
                    return if (mangaCount > 0 && position in 0 until mangaCount) {
                        1
                    } else {
                        gridSpanCount
                    }
                }
            }

        mangaAdapter.onItemsArrived {
            concatAdapter.removeAdapter(initialLoadIndicatorAdapter)
        }

        // Listener to determine whether to shrink or expand FAB (Shrink after 1st item in list is no longer visible)
        binding.listManga.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisiblePosition =
                    gridLayoutManager.findFirstVisibleItemPosition()
                if (firstVisiblePosition != 0) {
                    parentViewModel.setShouldShrinkFab(true)
                } else {
                    parentViewModel.setShouldShrinkFab(false)
                }
            }
        })

        with(binding.listManga) {
            adapter = concatAdapter
            layoutManager = gridLayoutManager
            itemAnimator = null
        }
    }

    private fun initUi() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            mangaAdapter.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun observeUi() {
        val tabType = TabType.valueOf(arguments?.getString(TAB_TYPE) ?: "")
        val mangaList = when (tabType) {
            TabType.TRENDING -> viewModel.getTrendingManga()
            TabType.LATEST -> viewModel.getRecentManga()
            TabType.NEW -> viewModel.getNewManga()
            else -> throw IllegalArgumentException()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    mangaList.collectLatest { mangaAdapter.submitData(it) }
                }
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAB_TYPE = "manga_type"
        private const val SOURCE = "source"

        @JvmStatic
        fun newInstance(source: Source, type: TabType) =
            SimpleMangaListFragment().apply {
                arguments = Bundle().apply {
                    putString(SOURCE, source.toString())
                    putString(TAB_TYPE, type.name)
                }
            }
    }
}
