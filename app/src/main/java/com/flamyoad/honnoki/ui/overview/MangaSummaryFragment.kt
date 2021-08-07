package com.flamyoad.honnoki.ui.overview

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
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.databinding.FragmentMangaSummaryBinding
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.data.entities.Genre
import com.flamyoad.honnoki.ui.lookup.MangaLookupActivity
import com.flamyoad.honnoki.ui.lookup.model.LookupType
import com.flamyoad.honnoki.ui.overview.adapter.*
import com.flamyoad.honnoki.ui.overview.model.ChapterListStyle
import com.flamyoad.honnoki.ui.overview.model.ReaderChapter
import com.flamyoad.honnoki.ui.reader.ReaderActivity
import com.flamyoad.honnoki.utils.extensions.toast
import jp.wasabeef.recyclerview.animators.FadeInAnimator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import java.lang.IllegalArgumentException

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class MangaSummaryFragment : Fragment() {

    var style = ChapterListStyle.GRID

    private var _binding: FragmentMangaSummaryBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val mangaSource: String by lazy {
        requireActivity().intent.getStringExtra(MangaOverviewActivity.MANGA_SOURCE) ?: ""
    }

    private val viewModel: MangaOverviewViewModel by sharedViewModel {
        parametersOf(mangaSource)
    }

    private val mainHeaderAdapter by lazy { MainHeaderAdapter() }
    private val mangaSummaryAdapter by lazy { MangaSummaryAdapter(this::lookupMangaByGenre) }
    private val chapterListHeaderAdapter by lazy {
        ChapterListHeaderAdapter(
            viewModel::toggleChapterListSort,
            this::changeChapterListStyle
        )
    }
    private val languageFilterAdapter by lazy {
        LanguageFilterAdapter(requireContext(), viewModel::setChapterLanguageFilter)
    }
    private val chapterListLoadingAdapter by lazy { ChapterListLoadingAdapter() }
    private val chapterGridAdapter by lazy { ChapterGridAdapter(this::onChapterClick) }
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
    ): View {
        _binding = FragmentMangaSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi(style)
        observeUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUi(chapterListStyle: ChapterListStyle) {
        // Sources other than MD dont need lang filters
        if (Source.valueOf(mangaSource) == Source.MANGADEX) {
            concatAdapter.addAdapter(languageFilterAdapter)
        }

        val fullSpanCount = 3

        val adapterList = concatAdapter.adapters
        val gridLayoutManager = GridLayoutManager(requireContext(), fullSpanCount)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // This is for the first item in grid (Full span width). List is always full span
                if (position == adapterList.size) {
                    return fullSpanCount
                }

                // This is for the second until last item in grid (1/3 span width). List is always full span
                if (position > adapterList.size) {
                    return when (chapterListStyle) {
                        ChapterListStyle.GRID -> 1
                        ChapterListStyle.LIST -> fullSpanCount
                    }
                }

                return when (adapterList[position]) {
                    is MainHeaderAdapter -> fullSpanCount
                    is MangaSummaryAdapter -> fullSpanCount
                    is ChapterListHeaderAdapter -> fullSpanCount
                    is LanguageFilterAdapter -> fullSpanCount
                    is ChapterListLoadingAdapter -> fullSpanCount
                    is ChapterGridAdapter -> fullSpanCount
                    is ChapterListAdapter -> fullSpanCount
                    else -> throw IllegalArgumentException("Unknown adapter type")
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
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.mangaOverview.collectLatest {
                        mangaSummaryAdapter.setMangaOverview(State.Success(it))
                    }
                }
            }
        }

        viewModel.genreList.observe(viewLifecycleOwner) {
            mangaSummaryAdapter.setGenres(State.Success(it))
        }

        viewModel.languageList.observe(viewLifecycleOwner) {
            languageFilterAdapter.items = it
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.chapterList.collectLatest {
                    when (it) {
                        is State.Loading -> {
                            concatAdapter.addAdapter(chapterListLoadingAdapter)
                        }
                        is State.Success -> {
                            concatAdapter.apply {
                                removeAdapter(chapterListLoadingAdapter)
                                when (style) {
                                    ChapterListStyle.GRID -> addAdapter(chapterGridAdapter)
                                    ChapterListStyle.LIST -> addAdapter(chapterListAdapter)
                                }
                            }
                            chapterListHeaderAdapter.setItem(it.value)
                            chapterGridAdapter.submitList(it.value)
                            chapterListAdapter.submitList(it.value)
                        }
                    }
                }
            }
        }
    }

    private fun onChapterClick(chapter: ReaderChapter) {
        val overview = viewModel.overview

        val overviewId = overview.id ?: return
        val chapterId = chapter.id ?: return
        val source = overview.source ?: return

        ReaderActivity.start(requireContext(), chapterId, overviewId, source)
    }

    private fun changeChapterListStyle() {
        concatAdapter.apply {
            removeAdapter(chapterGridAdapter)
            removeAdapter(chapterListAdapter)
        }

        if (style == ChapterListStyle.GRID) {
            initUi(ChapterListStyle.LIST)
            style = ChapterListStyle.LIST
            concatAdapter.addAdapter(chapterListAdapter)
        } else {
            initUi(ChapterListStyle.GRID)
            style = ChapterListStyle.GRID
            concatAdapter.addAdapter(chapterGridAdapter)
        }
    }

    private fun lookupMangaByGenre(genre: Genre) {
        val source = Source.valueOf(mangaSource)

        MangaLookupActivity.startActivity(
            requireContext(),
            genre.link,
            genre.name,
            source,
            LookupType.GENRE
        )
    }

    companion object {
        @JvmStatic
        fun newInstance() = MangaSummaryFragment()
    }
}
