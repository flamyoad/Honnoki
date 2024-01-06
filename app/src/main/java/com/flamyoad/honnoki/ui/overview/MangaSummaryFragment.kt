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
import com.flamyoad.honnoki.ui.overview.model.ChapterListState
import com.flamyoad.honnoki.ui.overview.model.ChapterListDisplayMode
import com.flamyoad.honnoki.ui.overview.model.ReaderChapter
import com.flamyoad.honnoki.ui.reader.ReaderActivity
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

    var displayMode = ChapterListDisplayMode.GRID

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
            this::toggleChapterListDisplayMode
        )
    }
    private val languageFilterAdapter by lazy {
        LanguageFilterAdapter(requireContext(), viewModel::setChapterLanguageFilter)
    }
    private val chapterListLoadingAdapter by lazy { ChapterListLoadingAdapter() }
    private val chapterListEmptyAdapter by lazy { ChapterListEmptyAdapter() }
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("displayMode", displayMode)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.getSerializable("displayMode").let {
            if (it is ChapterListDisplayMode) {
                displayMode = it
            }
        }
        initUi(displayMode)
        observeUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUi(chapterListStyle: ChapterListDisplayMode) {
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
                        ChapterListDisplayMode.GRID -> 1
                        ChapterListDisplayMode.LIST -> fullSpanCount
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
                    is ChapterListEmptyAdapter -> fullSpanCount
                    else -> throw IllegalArgumentException("Unknown adapter type")
                }
            }
        }

        with(binding.listChapter) {
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
                            setChapterListState(ChapterListState.LOADING)
                        }
                        is State.Success -> {
                            concatAdapter.apply {
                                removeAdapter(chapterListLoadingAdapter)
                                when (displayMode) {
                                    ChapterListDisplayMode.GRID -> addAdapter(chapterGridAdapter)
                                    ChapterListDisplayMode.LIST -> addAdapter(chapterListAdapter)
                                }
                            }
                            chapterListHeaderAdapter.setItem(it.value.size)
                            chapterGridAdapter.submitList(it.value)
                            chapterListAdapter.submitList(it.value)
                        }
                        else -> {}
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.noChaptersFound().collectLatest { noChapters ->
                    if (noChapters) {
                        setChapterListState(ChapterListState.EMPTY)
                    }
                }
            }
        }
    }

    private fun setChapterListState(state: ChapterListState) {
        concatAdapter.apply {
            removeAdapter(chapterListLoadingAdapter)
            removeAdapter(chapterListAdapter)
            removeAdapter(chapterGridAdapter)
            removeAdapter(chapterListEmptyAdapter)
        }

        when (state) {
            ChapterListState.LOADING -> {
                concatAdapter.addAdapter(chapterListLoadingAdapter)
            }
            ChapterListState.EMPTY -> {
                concatAdapter.addAdapter(chapterListEmptyAdapter)
            }
            // This one will reinitialize the layout manager. Used when switching between LIST or GRID.
            ChapterListState.CONTENT_RENEW_LAYOUTMANAGER -> {
                initUi(displayMode)
                when (displayMode) {
                    ChapterListDisplayMode.GRID -> concatAdapter.addAdapter(chapterGridAdapter)
                    ChapterListDisplayMode.LIST -> concatAdapter.addAdapter(chapterListAdapter)
                }
            }
            // This one does not reinitialize the layout manager
            ChapterListState.CONTENT_REUSE_LAYOUTMANAGER -> {
                when (displayMode) {
                    ChapterListDisplayMode.GRID -> concatAdapter.addAdapter(chapterGridAdapter)
                    ChapterListDisplayMode.LIST -> concatAdapter.addAdapter(chapterListAdapter)
                }
            }
        }
    }

    private fun toggleChapterListDisplayMode() {
        concatAdapter.apply {
            removeAdapter(chapterGridAdapter)
            removeAdapter(chapterListAdapter)
        }
        if (displayMode == ChapterListDisplayMode.LIST) {
            displayMode = ChapterListDisplayMode.GRID
            initUi(ChapterListDisplayMode.GRID)
            concatAdapter.addAdapter(chapterGridAdapter)
        } else {
            displayMode = ChapterListDisplayMode.LIST
            initUi(ChapterListDisplayMode.LIST)
            concatAdapter.addAdapter(chapterListAdapter)
        }
    }

    private fun onChapterClick(chapter: ReaderChapter) {
        val overview = viewModel.overview

        val overviewId = overview.id ?: return
        val chapterId = chapter.id ?: return
        val source = overview.source ?: return

        ReaderActivity.start(requireContext(), chapterId, overviewId, source)
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
