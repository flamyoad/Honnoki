package com.flamyoad.honnoki.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.adapter.HomeListFragmentAdapter
import com.flamyoad.honnoki.adapter.MangaAdapter
import com.flamyoad.honnoki.databinding.FragmentHomeBinding
import com.flamyoad.honnoki.databinding.FragmentHomeListBinding
import com.flamyoad.honnoki.model.Manga
import com.flamyoad.honnoki.model.TabType
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeListFragment : Fragment() {
    private val viewModel: HomeViewModel by activityViewModels()
    private val binding by viewLifecycleLazy { FragmentHomeListBinding.bind(requireView()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val adapter = MangaAdapter(this::openManga)
        val layoutManager = GridLayoutManager(requireContext(), 2)

        with(binding.listManga) {
            this.adapter = adapter
            this.layoutManager = layoutManager
        }

        lifecycleScope.launch {
            viewModel.getRecentManga().collectLatest {
                adapter.submitData(it)
            }
        }

        // Listener to determine whether to shrink or expand FAB (Shrink after 1st item in list is no longer visible)
        binding.listManga.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                if (firstVisiblePosition != -1) {
                    viewModel.setShouldShrinkFab(true)
                } else {
                    viewModel.setShouldShrinkFab(false)
                }
            }
        })
    }

    private fun openManga(manga: Manga) {

    }

    companion object {
        const val TAB_GENRE = "HomeListFragment.TAB_GENRE"
        const val TAB_TYPE = "HomeListFragment.TAB_TYPE"

        @JvmStatic
        fun newInstance(tab: TabType) =
            HomeListFragment().apply {
                arguments = bundleOf(
                    TAB_GENRE to tab.genre,
                    TAB_TYPE to tab.type
                )
            }
    }
}
