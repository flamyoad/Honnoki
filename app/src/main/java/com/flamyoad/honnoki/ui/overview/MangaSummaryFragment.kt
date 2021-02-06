package com.flamyoad.honnoki.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.adapter.GenreListAdapter
import com.flamyoad.honnoki.databinding.FragmentMangaSummaryBinding
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class MangaSummaryFragment : Fragment() {
    private val viewModel: MangaOverviewViewModel by activityViewModels()
    private val binding by viewLifecycleLazy { FragmentMangaSummaryBinding.bind(requireView()) }

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
        val genreAdapter = GenreListAdapter()
        val flexLayoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }

        with(binding.listGenres) {
            adapter = genreAdapter
            layoutManager = flexLayoutManager
        }

        viewModel.genreList().observe(viewLifecycleOwner, Observer {
            genreAdapter.setList(it)
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MangaSummaryFragment()
    }
}
