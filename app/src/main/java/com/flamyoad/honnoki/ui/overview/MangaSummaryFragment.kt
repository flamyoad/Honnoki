package com.flamyoad.honnoki.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.adapter.GenreListAdapter
import com.flamyoad.honnoki.databinding.FragmentMangaSummaryBinding
import com.flamyoad.honnoki.model.MangaOverview
import com.flamyoad.honnoki.model.State
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.kennyc.view.MultiStateView

@ExperimentalPagingApi
class MangaSummaryFragment : Fragment() {
    private val viewModel: MangaOverviewViewModel by activityViewModels()
    private val binding by viewLifecycleLazy { FragmentMangaSummaryBinding.bind(requireView()) }

    private var genreAdapter: GenreListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manga_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        genreAdapter = GenreListAdapter()
        val flexLayoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }

        with(binding.listGenres) {
            adapter = genreAdapter
            layoutManager = flexLayoutManager
        }

        viewModel.mangaOverview().observe(viewLifecycleOwner) {
            when (it) {
                is State.Success -> { showMangaOverview(it.value) }
                is State.Error -> { binding.multiStateView.viewState = MultiStateView.ViewState.ERROR }
                is State.Loading -> { binding.multiStateView.viewState = MultiStateView.ViewState.LOADING }
            }
        }
    }

    private fun showMangaOverview(overview: MangaOverview) {
        genreAdapter?.setList(overview.genres)

        with(binding) {
            multiStateView.viewState = MultiStateView.ViewState.CONTENT
            expandableTextView.text = overview.summary
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MangaSummaryFragment()
    }
}
