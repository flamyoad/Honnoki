package com.flamyoad.honnoki.ui.reader

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.data.model.Source
import com.flamyoad.honnoki.databinding.ActivityReaderBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit

@ExperimentalPagingApi
class ReaderActivity : AppCompatActivity() {

    private var _binding: ActivityReaderBinding? = null
    val binding get() = requireNotNull(_binding)

    private val source: String by lazy {
        intent.getStringExtra(MANGA_SOURCE) ?: ""
    }

    private val viewModel: ReaderViewModel by viewModel {
        parametersOf(source)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        initUi()
        observeUi()

        if (savedInstanceState == null) {
            val frameFragment = VerticalScrollingReaderFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, frameFragment)
                .commit()
        }

        viewModel.fetchChapterList(intent.getLongExtra(OVERVIEW_ID, -1L))
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

            seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        if (progress == 0) {
                            viewModel.setCurrentPageNumber(1)
                        } else {
                            viewModel.setCurrentPageNumber(progress)
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // User has stopped moving. Move to the image selected
                    val progress = seekBar?.progress ?: return
                    if (progress == 0) {
                        viewModel.setSeekbarScrolledPosition(1)
                    } else {
                        viewModel.setSeekbarScrolledPosition(progress)
                    }
                }
            })
        }
    }

    private fun observeUi() {
        lifecycleScope.launchWhenResumed {
            viewModel.mangaOverview.collectLatest {
                binding.txtToolbarMangaTitle.text = it.mainTitle
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.sideKickVisibility().collectLatest {
                toggleSidekickVisibility(it)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.currentPageIndicator
                .debounce(50)
                .collectLatest {
                    binding.txtCurrentPageMini.text = it
                }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.currentPageIndicator
                .collectLatest {
                    binding.txtSeekbarCurrentPage.text = it
                }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.totalPageNumber.collectLatest {
                binding.seekbar.max = it
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.currentPageNumber().collectLatest {
                binding.seekbar.progress = it
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.currentPageNumber()
                .debounce(250)
                .collectLatest {
                    viewModel.saveLastReadPage(it)
                }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.currentChapterShown().collectLatest {
                binding.txtToolbarChapterTitle.text = it.title
                binding.txtCurrentChapterMini.text = it.title
            }
        }
    }

    private fun toggleSidekickVisibility(isVisible: Boolean) {
        TransitionManager.beginDelayedTransition(binding.appBarLayout, Slide(Gravity.TOP))
        TransitionManager.beginDelayedTransition(binding.seekbarLayout, Slide(Gravity.BOTTOM))

        // Hide the mini page number info on bottom right when the large bottom bar is visible
        binding.bottomRightInfoView.isVisible = !isVisible

        binding.appBarLayout.isVisible = isVisible
        binding.seekbarLayout.isVisible = isVisible
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val CHAPTER_ID = "chapter_id"
        const val OVERVIEW_ID = "overview_id"
        const val START_AT_PAGE = "start_at_page"
        const val MANGA_SOURCE = "manga_source"

        fun start(
            context: Context,
            chapterId: Long,
            overviewId: Long,
            startAtPage: Int,
            source: Source
        ) {
            val intent = Intent(context, ReaderActivity::class.java)
            intent.apply {
                putExtra(CHAPTER_ID, chapterId)
                putExtra(OVERVIEW_ID, overviewId)
                putExtra(START_AT_PAGE, startAtPage)
                putExtra(MANGA_SOURCE, source.toString())
            }
            context.startActivity(intent)
        }
    }
}
