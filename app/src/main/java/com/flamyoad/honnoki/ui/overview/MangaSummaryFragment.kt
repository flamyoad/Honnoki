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
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.FragmentMangaSummaryBinding
import com.flamyoad.honnoki.model.Chapter
import com.flamyoad.honnoki.model.State
import com.flamyoad.honnoki.ui.overview.adapter.*
import com.flamyoad.honnoki.ui.reader.ReaderActivity
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy
import java.io.Reader

@ExperimentalPagingApi
class MangaSummaryFragment : Fragment() {
    private val viewModel: MangaOverviewViewModel by activityViewModels()
    private val binding by viewLifecycleLazy { FragmentMangaSummaryBinding.bind(requireView()) }

    private val mainHeaderAdapter by lazy { MainHeaderAdapter() }
    private val mangaSummaryAdapter by lazy { MangaSummaryAdapter() }
    private val chapterListHeaderAdapter by lazy { ChapterListHeaderAdapter(viewModel::sortChapterList) }

    private val chapterListLoadingAdapter by lazy { ChapterListLoadingAdapter() }
    private val chapterListAdapter by lazy { ChapterListAdapter(this::onChapterClick) }

    private val concatAdapter by lazy {
        ConcatAdapter(
            mainHeaderAdapter,
            mangaSummaryAdapter,
            chapterListHeaderAdapter,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manga_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        observeUi()
    }

    private fun initUi() {
        val spanCount = 3
        val gridLayoutManager = GridLayoutManager(requireContext(), spanCount)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (position) {
                    0 -> spanCount
                    1 -> spanCount
                    2 -> spanCount
                    3 -> spanCount
                    else -> 1
                }
            }
        }

        with(binding.contentList) {
            adapter = concatAdapter
            layoutManager = gridLayoutManager
        }
    }

    private fun observeUi() {
        viewModel.mangaOverview.observe(viewLifecycleOwner) {
            mangaSummaryAdapter.setMangaOverview(State.Success(it))
        }

        viewModel.genreList.observe(viewLifecycleOwner) {
            mangaSummaryAdapter.setGenres(State.Success(it))
        }

        viewModel.chapterList.observe(viewLifecycleOwner) {
            when (it) {
                is State.Loading -> {
                    concatAdapter.addAdapter(chapterListLoadingAdapter)
                }
                is State.Success -> {
                    concatAdapter.apply {
                        removeAdapter(chapterListLoadingAdapter)
                        addAdapter(chapterListAdapter)
                    }
                    chapterListHeaderAdapter.setItem(it.value)
                    chapterListAdapter.submitList(it.value)
                }
            }
        }
    }

    private fun onChapterClick(chapter: Chapter) {
        val mangaTitle = requireActivity().intent.getStringExtra(MangaOverviewActivity.MANGA_TITLE)

        val intent = Intent(requireContext(), ReaderActivity::class.java)
        intent.apply {
            putExtra(ReaderActivity.CHAPTER_ID, chapter.id)
            putExtra(ReaderActivity.CHAPTER_URL, chapter.link)
            putExtra(ReaderActivity.CHAPTER_TITLE, chapter.title)
            putExtra(ReaderActivity.MANGA_TITLE, mangaTitle)
            putExtra(ReaderActivity.OVERVIEW_ID, viewModel.overviewId)
        }
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MangaSummaryFragment()
    }
}
