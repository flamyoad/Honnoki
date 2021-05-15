package com.flamyoad.honnoki.ui.overview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager

import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.adapter.ChapterListAdapter
import com.flamyoad.honnoki.databinding.FragmentMangaChapterListBinding
import com.flamyoad.honnoki.model.Chapter
import com.flamyoad.honnoki.model.State
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy
import com.kennyc.view.MultiStateView

@ExperimentalPagingApi
class MangaChapterListFragment : Fragment() {
    private val viewModel: MangaOverviewViewModel by activityViewModels()
    private val binding by viewLifecycleLazy { FragmentMangaChapterListBinding.bind(requireView()) }

    private val chapterAdapter = ChapterListAdapter(this::onChapterClick)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manga_chapter_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.listChapters) {
            adapter = chapterAdapter
            layoutManager = LinearLayoutManager(this@MangaChapterListFragment.requireContext())
        }

        viewModel.chapterList().observe(viewLifecycleOwner) {
            when (it) {
                is State.Success -> showChapterList(it.value)
                is State.Loading -> binding.multiStateView.viewState = MultiStateView.ViewState.LOADING
                is State.Error -> binding.multiStateView.viewState = MultiStateView.ViewState.ERROR
            }
        }
    }

    private fun showChapterList(list: List<Chapter>) {
        chapterAdapter.submitList(list)
        binding.multiStateView.viewState = MultiStateView.ViewState.CONTENT
    }

    private fun onChapterClick(chapter: Chapter) {

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MangaChapterListFragment()
    }
}
