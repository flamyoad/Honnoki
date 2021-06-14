package com.flamyoad.honnoki.ui.overview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.FragmentMangaSummaryBinding
import com.flamyoad.honnoki.data.model.Chapter
import com.flamyoad.honnoki.data.model.State
import com.flamyoad.honnoki.ui.overview.adapter.*
import com.flamyoad.honnoki.ui.overview.model.ReaderChapter
import com.flamyoad.honnoki.ui.reader.ReaderActivity
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy
import jp.wasabeef.recyclerview.animators.FadeInAnimator
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

@ExperimentalPagingApi
class MangaSummaryFragment : Fragment() {
    private val mangaSource: String by lazy {
        requireActivity().intent.getStringExtra(MangaOverviewActivity.MANGA_SOURCE) ?: ""
    }

    private val viewModel: MangaOverviewViewModel by sharedViewModel {
        parametersOf(mangaSource)
    }

    private val binding by viewLifecycleLazy { FragmentMangaSummaryBinding.bind(requireView()) }

    private val mainHeaderAdapter by lazy { MainHeaderAdapter() }
    private val mangaSummaryAdapter by lazy { MangaSummaryAdapter() }
    private val chapterListHeaderAdapter by lazy { ChapterListHeaderAdapter(viewModel::toggleChapterListSort) }

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
            itemAnimator = FadeInAnimator()
        }
    }

    private fun observeUi() {
        lifecycleScope.launchWhenResumed {
            viewModel.mangaOverview.collectLatest {
                mangaSummaryAdapter.setMangaOverview(State.Success(it))
            }
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

    private fun onChapterClick(chapter: ReaderChapter) {
        val overview = viewModel.overview

        val overviewId = overview.id ?: return
        val chapterId = chapter.id ?: return
        val source = overview.source ?: return

        val startAtPage = if (overview.lastReadChapterId == chapterId) {
            overview.lastReadPageNumber
        } else {
            0
        }

        ReaderActivity.start(requireContext(), chapterId, overviewId, startAtPage, source)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MangaSummaryFragment()
    }
}
