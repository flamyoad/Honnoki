package com.flamyoad.honnoki.ui.reader

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.data.entities.Chapter
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.databinding.ActivityReaderBinding
import com.flamyoad.honnoki.ui.reader.model.LoadType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ExperimentalPagingApi
class ReaderActivity : AppCompatActivity() {

    private var _binding: ActivityReaderBinding? = null
    val binding get() = requireNotNull(_binding)

    private val source: String by lazy { intent.getStringExtra(MANGA_SOURCE) ?: "" }

    private val viewModel: ReaderViewModel by viewModel { parametersOf(source) }

    private var volumeButtonScroller: VolumeButtonScroller? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Helps to survive process death by checking whether the initial id is -1
        if (viewModel.overviewId == -1L) {
            val frameFragment = VerticalScrollingReaderFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, frameFragment, VerticalScrollingReaderFragment.TAG)
                .commitNow()
        }
        val listener =
            supportFragmentManager.findFragmentById(R.id.container) as VolumeButtonScroller.Listener
        volumeButtonScroller = VolumeButtonScroller(listener, getKoin().get())

        initUi()
        observeUi()

        viewModel.fetchChapterList(intent.getLongExtra(OVERVIEW_ID, -1))

        val chapterIdPickedByUser = intent.getLongExtra(CHAPTER_ID, -1)
        if (savedInstanceState == null) {
            viewModel.fetchChapterImages(chapterIdPickedByUser, LoadType.INITIAL)
        } else {
            viewModel.restoreLastReadChapter(intent.getLongExtra(OVERVIEW_ID, -1), chapterIdPickedByUser)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.currentChapterShown().value.let {
            if (it == Chapter.empty()) return
            viewModel.saveLastReadChapter(it)
        }
        viewModel.currentPageNumber().value.let {
            if (it == 0) return
            viewModel.saveLastReadPage(it)
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        volumeButtonScroller?.let {
            val shouldSwallowKeyEvent = it.sendKeyEvent(event)
            if (shouldSwallowKeyEvent) {
                return true
            } else {
                return super.dispatchKeyEvent(event)
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_reader_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    private fun initUi() {
        with(binding) {
            bottomSheetOpener.setOnClickListener {
                viewModel.setSideKickVisibility(true)
            }

            btnToFirstPage.setOnClickListener {
                viewModel.goToFirstPage()
            }

            btnToLastPage.setOnClickListener {
                viewModel.goToLastPage()
            }

            if (viewModel.extraSpaceAtBottomIndicator) {
                bottomInfoWidget.updatePadding(right = 32)
            }

            seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        viewModel.setCurrentPageNumber(progress + 1)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // User has stopped moving. Move to the image selected
                    val progress = seekBar?.progress ?: return
                    viewModel.setSeekbarScrolledPosition(progress + 1)
                }
            })
        }
    }

    private fun observeUi() {
        lifecycleScope.launchWhenResumed {
            viewModel.mangaOverview
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collectLatest { binding.txtToolbarMangaTitle.text = it.mainTitle }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.sideKickVisibility()
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collectLatest { toggleSidekickVisibility(it) }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.currentPageIndicator
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .debounce(50)
                .collectLatest { binding.bottomInfoWidget.currentPage = it }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.currentPageIndicator
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collectLatest { binding.txtSeekbarCurrentPage.text = it }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.totalPageNumber
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collectLatest { binding.seekbar.max = it - 1 }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.currentPageNumber()
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collectLatest { binding.seekbar.progress = it }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.currentChapterShown()
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collectLatest {
                    binding.txtToolbarChapterTitle.text = it.title
                    binding.bottomInfoWidget.currentChapter = it.title
                    viewModel.markChapterAsRead(it)
                }
        }
    }

    // https://stackoverflow.com/questions/4503039/layout-animation-not-working-on-first-run
    // Reason is because the view is not initially inflated when View.GONE is set. Use View.INVISIBLE instead.
    private fun toggleSidekickVisibility(isVisible: Boolean) {
        binding.bottomInfoWidget.isVisible = !isVisible

        val visibility = when (isVisible) {
            true -> View.VISIBLE
            false -> View.INVISIBLE
        }
        TransitionManager.beginDelayedTransition(binding.appBarLayout, Slide(Gravity.TOP))
        TransitionManager.beginDelayedTransition(binding.seekbarLayout, Slide(Gravity.BOTTOM))
        binding.appBarLayout.visibility = visibility
        binding.seekbarLayout.visibility = visibility
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val CHAPTER_ID = "chapter_id"
        const val OVERVIEW_ID = "overview_id"
        const val MANGA_SOURCE = "manga_source"

        fun start(
            context: Context,
            chapterId: Long,
            overviewId: Long,
            source: Source
        ) {
            val intent = Intent(context, ReaderActivity::class.java)
            intent.apply {
                putExtra(CHAPTER_ID, chapterId)
                putExtra(OVERVIEW_ID, overviewId)
                putExtra(MANGA_SOURCE, source.toString())
            }
            context.startActivity(intent)
        }
    }
}
