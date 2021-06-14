package com.flamyoad.honnoki.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.flamyoad.honnoki.adapter.MangaLoadStateAdapter
import com.flamyoad.honnoki.adapter.RecentMangaListAdapter
import com.flamyoad.honnoki.databinding.FragmentSimpleMangaListBinding
import com.flamyoad.honnoki.data.model.Manga
import com.flamyoad.honnoki.data.model.MangaType
import com.flamyoad.honnoki.di.KoinConstants
import com.flamyoad.honnoki.ui.overview.MangaOverviewActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

@ExperimentalPagingApi
class SimpleMangaListFragment : Fragment() {

    private val viewModel: HomeViewModel by sharedViewModel  {
        parametersOf(KoinConstants.MANGAKALOT)
    }

    private var _binding: FragmentSimpleMangaListBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val gridLayoutManager by lazy { GridLayoutManager(requireContext(), 3) }

    override fun onResume() {
        super.onResume()
        if (gridLayoutManager.findFirstVisibleItemPosition() == 0) {
            viewModel.setShouldShrinkFab(false)
        } else {
            viewModel.setShouldShrinkFab(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSimpleMangaListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val mangaAdapter = RecentMangaListAdapter(this::openManga).apply {
            withLoadStateFooter(MangaLoadStateAdapter { this.retry() })
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        with(binding.listManga) {
            this.adapter = mangaAdapter
            this.layoutManager = gridLayoutManager
            itemAnimator = null
        }

        val mangaType = MangaType.valueOf(arguments?.getString(MANGA_TYPE) ?: "")

        lifecycleScope.launch {
            val mangaList = when (mangaType) {
                MangaType.TRENDING -> viewModel.getTrendingManga()
                MangaType.RECENTLY -> viewModel.getRecentManga()
                MangaType.NEW -> viewModel.getNewManga()
                else -> throw IllegalArgumentException()
            }
            mangaList.collectLatest {
                mangaAdapter.submitData(it)
            }
        }

        // Listener to determine whether to shrink or expand FAB (Shrink after 1st item in list is no longer visible)
        binding.listManga.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisiblePosition = gridLayoutManager.findFirstVisibleItemPosition()
                if (firstVisiblePosition != 0) {
                    viewModel.setShouldShrinkFab(true)
                } else {
                    viewModel.setShouldShrinkFab(false)
                }
            }
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            mangaAdapter.refresh()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val MANGA_TYPE = "manga_type"

        @JvmStatic
        fun newInstance(type: MangaType) =
            SimpleMangaListFragment().apply {
                arguments = Bundle().apply {
                    putString(MANGA_TYPE, type.name)
                }
            }
    }
}
