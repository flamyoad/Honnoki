package com.flamyoad.honnoki.ui.reader

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.data.entities.Chapter
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.databinding.ActivityReaderBinding
import com.flamyoad.honnoki.dialog.BookmarkDialog
import com.flamyoad.honnoki.ui.reader.model.LoadType
import com.flamyoad.honnoki.ui.reader.model.ReaderOrientation
import com.flamyoad.honnoki.ui.reader.model.ReaderViewMode
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ExperimentalPagingApi
class ReaderActivity : AppCompatActivity() {

    private var _binding: ActivityReaderBinding? = null
    val binding get() = requireNotNull(_binding)

    private val source: String by lazy {
        intent.getStringExtra(MANGA_SOURCE) ?: ""
    }

    private val viewModel: ReaderViewModel by viewModel { parametersOf(source) }

    private var volumeButtonScroller: VolumeButtonScroller? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        viewModel.orientation.observe(this) {
            if (it == null) return@observe
            requestedOrientation = when (it) {
                ReaderOrientation.FREE -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                ReaderOrientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                ReaderOrientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            initUi()
            observeUi()
        }

        viewModel.fetchChapterList(intent.getLongExtra(OVERVIEW_ID, -1))

        val chapterIdPickedByUser = intent.getLongExtra(CHAPTER_ID, -1)
        if (savedInstanceState == null) {
            viewModel.fetchChapterImages(
                chapterIdPickedByUser,
                LoadType.INITIAL
            )
        } else {
            viewModel.restoreLastReadChapter(
                intent.getLongExtra(
                    OVERVIEW_ID,
                    -1
                ), chapterIdPickedByUser
            )
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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
            appBarLayout.outlineProvider = null

            bottomSheetOpener.setOnClickListener {
                viewModel.setSideKickVisibility(true)
            }

            bottomInfoWidget.apply {
                if (viewModel.extraSpaceAtBottomIndicator) {
                    updatePadding(right = 32)
                }
            }

            bottomActionMenu.apply {
                onChapterListClick = { showChapterListDialog() }
                onViewModeClick = { showViewModeDialog() }
                onOrientationClick = { showOrientationDialog() }
                onBookmarkClick = { showBookmarkDialog() }
            }

            readerSeekbar.apply {
                onLeftButtonClick = { viewModel.goToFirstPage() }
                onRightButtonClick = { viewModel.goToLastPage() }
                onUserProgressChanged = { progress ->
                    viewModel.setCurrentPageNumber(progress + 1)
                }
                onStopTrackingTouch = {
                    viewModel.setSeekbarScrolledPosition(readerSeekbar.current + 1)
                }
            }
        }
    }

    private fun observeUi() {
        lifecycleScope.launchWhenResumed {
            viewModel.mangaOverview
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collectLatest {
                    binding.txtToolbarMangaTitle.text = it.mainTitle
                }
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
                .collectLatest { binding.readerSeekbar.text = it }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.totalPageNumber
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collectLatest {
                    // Seekbar max must be set first before progress value!!
                    binding.readerSeekbar.max = it - 1
                    binding.readerSeekbar.current =
                        viewModel.currentPageNumber().value
                }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.currentPageNumber()
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collectLatest { binding.readerSeekbar.current = it }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.currentPageNumber()
                .debounce(500)
                .collectLatest { viewModel.saveLastReadPage(it) }
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

        lifecycleScope.launchWhenResumed {
            viewModel.currentChapterShown()
                .debounce(500)
                .collectLatest { viewModel.saveLastReadChapter(it) }
        }

        viewModel.viewMode.observe(this) {
            initReaderScreen(it)
        }
    }

    private fun initReaderScreen(viewMode: ReaderViewMode) {
        val frameFragment = when (viewMode) {
            ReaderViewMode.HORIZONTAL ->
                SwipeReaderFragment.newInstance(
                    SwipeDirection.HORIZONTAL
                )
            ReaderViewMode.VERTICAL ->
                SwipeReaderFragment.newInstance(
                    SwipeDirection.VERTICAL
                )
            ReaderViewMode.CONTINUOUS_SCROLLING -> VerticalScrollingReaderFragment.newInstance()
        }
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                frameFragment,
                VerticalScrollingReaderFragment.TAG
            ).commitNow()

        val listener =
            supportFragmentManager.findFragmentById(R.id.container) as VolumeButtonScroller.Listener
        volumeButtonScroller = VolumeButtonScroller(listener, getKoin().get())
    }

    // https://stackoverflow.com/questions/4503039/layout-animation-not-working-on-first-run
    // Reason is because the view is not initially inflated when View.GONE is set. Use View.INVISIBLE instead.
    private fun toggleSidekickVisibility(isVisible: Boolean) {
        binding.bottomInfoWidget.isVisible = !isVisible

        val visibility = when (isVisible) {
            true -> View.VISIBLE
            false -> View.INVISIBLE
        }
        TransitionManager.beginDelayedTransition(
            binding.appBarLayout,
            Slide(Gravity.TOP)
        )
        TransitionManager.beginDelayedTransition(
            binding.bottomMenu,
            Slide(Gravity.BOTTOM)
        )
        binding.appBarLayout.visibility = visibility
        binding.bottomMenu.visibility = visibility
    }

    private fun showChapterListDialog() {
//        binding.drawerLayout.openDrawer(GravityCompat.END)
        toggleSidekickVisibility(false)
    }

    private fun showViewModeDialog() {
        val viewModes = ReaderViewMode.values()
        val choices = viewModes.map { getString(it.stringId) }
        val currentIndex =
            viewModes.indexOfFirst { it == viewModel.getViewModeBlocking() }
        MaterialDialog(this).show {
            title(text = context.getString(R.string.reader_viewmode))
            listItemsSingleChoice(
                items = choices,
                initialSelection = currentIndex,
                waitForPositiveButton = true,
            ) { dialog, index, text ->
                viewModel.editViewMode(viewModes[index])
            }
        }
    }

    private fun showOrientationDialog() {
        val orientations = ReaderOrientation.values()
        val choices = orientations.map { getString(it.stringId) }
        val currentIndex =
            orientations.indexOfFirst { it == viewModel.getOrientationBlocking() }
        MaterialDialog(this).show {
            title(text = context.getString(R.string.reader_orientation))
            listItemsSingleChoice(
                items = choices,
                initialSelection = currentIndex,
                waitForPositiveButton = true,
            ) { dialog, index, text ->
                viewModel.editOrientation(orientations[index])
            }
        }
    }

    private fun showBookmarkDialog() {
        val overviewId = viewModel.overviewId
        if (overviewId == -1L) return

        val dialog = BookmarkDialog.newInstance(overviewId)
        dialog.show(supportFragmentManager, "bookmark_dialog")
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
