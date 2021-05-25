package com.flamyoad.honnoki.ui.overview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.FragmentMangaSummaryBinding
import com.flamyoad.honnoki.model.Chapter
import com.flamyoad.honnoki.model.State
import com.flamyoad.honnoki.ui.overview.adapter.ChapterListAdapter
import com.flamyoad.honnoki.ui.overview.adapter.ChapterListHeaderAdapter
import com.flamyoad.honnoki.ui.overview.adapter.MainHeaderAdapter
import com.flamyoad.honnoki.ui.overview.adapter.MangaSummaryAdapter
import com.flamyoad.honnoki.ui.reader.ReaderActivity
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy

@ExperimentalPagingApi
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

        val mainHeaderAdapter = MainHeaderAdapter()
        val mangaSummaryAdapter = MangaSummaryAdapter()
        val chapterListHeaderAdapter = ChapterListHeaderAdapter(viewModel::sortChapterList)
        val chapterListAdapter = ChapterListAdapter(this::onChapterClick)

        val concatAdapter =
            ConcatAdapter(
                mainHeaderAdapter,
                mangaSummaryAdapter,
                chapterListHeaderAdapter,
                chapterListAdapter
            )

        val spanCount = 3
        val gridLayoutManager =  GridLayoutManager(requireContext(), spanCount)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (position) {
                    0 -> spanCount
                    1 -> spanCount
                    2 -> spanCount
                    else -> 1
                }
            }
        }

        with(binding.contentList) {
            adapter = concatAdapter
            layoutManager = gridLayoutManager
        }

        viewModel.mangaOverview().observe(viewLifecycleOwner) {
            mangaSummaryAdapter.setItem(it)
        }

        viewModel.chapterList().observe(viewLifecycleOwner) {
            chapterListHeaderAdapter.setItem(it)
            when (it) {
                is State.Success -> {
                    chapterListAdapter.submitList(it.value)
                }
            }
        }
    }

    private fun onChapterClick(chapter: Chapter) {
        val mangaTitle = requireActivity().intent.getStringExtra(MangaOverviewActivity.MANGA_TITLE)

        val intent = Intent(requireContext(), ReaderActivity::class.java)
        intent.apply {
            putExtra(ReaderActivity.CHAPTER_URL, chapter.link)
            putExtra(ReaderActivity.CHAPTER_TITLE, chapter.title)
            putExtra(ReaderActivity.MANGA_TITLE, mangaTitle)
        }
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MangaSummaryFragment()
    }
}
