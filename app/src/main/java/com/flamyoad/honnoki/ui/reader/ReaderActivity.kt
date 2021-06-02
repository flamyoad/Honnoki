package com.flamyoad.honnoki.ui.reader

import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.ViewPager2
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.ActivityReaderBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest

@ExperimentalPagingApi
class ReaderActivity : AppCompatActivity() {

    private var _binding: ActivityReaderBinding? = null
    val binding get() = requireNotNull(_binding)

    private val viewModel: ReaderViewModel by viewModels()

    private var scrollingFromSeekbar: Boolean = false

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
            val frameFragment = ReaderFrameFragment.newInstance()
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
            txtToolbarMangaTitle.text = intent.getStringExtra(MANGA_TITLE)
            txtToolbarChapterTitle.text = intent.getStringExtra(CHAPTER_TITLE)

            bottomSheetOpener.setOnClickListener {
                viewModel.setSideKickVisibility(true)
            }

            seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        viewModel.setCurrentPage(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    scrollingFromSeekbar = true
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // User has stopped moving. Move to the image selected
                    val progress = seekBar?.progress ?: return
                    viewModel.setSeekbarScrolledPosition(progress)
                }
            })
        }
    }

    /*
    Each collectLatest() must have its own coroutine scope.
    You cannot combine all of them in one scope like this:

        lifeCycleScope.launchWhenResumed {
             ... collectLatest { }
             ... collectLatest { }
             ... collectLatest { }
         }

     In this case, only the first collectLatest() will emit values
    */

    private fun observeUi() {
        lifecycleScope.launchWhenResumed {
            viewModel.sideKickVisibility().collectLatest {
                toggleSidekickVisibility(it)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.currentPageIndicator.collectLatest {
                with(binding) {
                    txtSeekbarCurrentPage.text = it
                    txtCurrentPageMini.text = it
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.totalPages().collectLatest {
                binding.seekbar.max = it + 1
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.currentPage().collectLatest {
                binding.seekbar.progress = it + 1
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
        const val MANGA_TITLE = "manga_title"
        const val CHAPTER_ID = "chapter_id"
        const val CHAPTER_TITLE = "chapter_title"
        const val CHAPTER_URL = "chapter_url"
        const val OVERVIEW_ID = "overview_id"
    }
}
