package com.flamyoad.honnoki.ui.reader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import androidx.appcompat.widget.AppCompatSeekBar
import android.view.MenuItem
import android.view.WindowManager
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.adapter.ReaderImageAdapter
import com.flamyoad.honnoki.databinding.ActivityReaderBinding
import com.flamyoad.honnoki.model.State
import com.flamyoad.honnoki.ui.overview.MangaOverviewActivity

@ExperimentalPagingApi
class ReaderActivity : AppCompatActivity() {
    private var _binding: ActivityReaderBinding? = null
    val binding get() = requireNotNull(_binding)

    private val viewModel: ReaderViewModel by viewModels()

    private lateinit var readerAdapter: ReaderImageAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var scrollingFromSeekbar: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        if (savedInstanceState == null) {
            val chapterUrl = intent.getStringExtra(CHAPTER_URL) ?: ""
            viewModel.fetchManga(chapterUrl)
            toggleSidekickVisibility(isVisible = false)
        }

        binding.txtToolbarMangaTitle.text = intent.getStringExtra(ReaderActivity.MANGA_TITLE)
        binding.txtToolbarChapterTitle.text = intent.getStringExtra(ReaderActivity.CHAPTER_TITLE)

        readerAdapter = ReaderImageAdapter()
        linearLayoutManager = LinearLayoutManager(this)

        with(binding.listImages) {
            adapter = readerAdapter
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(this@ReaderActivity, DividerItemDecoration.VERTICAL))
        }

        binding.listImages.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                syncPageNumber(linearLayoutManager.findFirstVisibleItemPosition())

                // Show the bottom bar when the scrolling is done by seekbar
                if (!scrollingFromSeekbar) {
                    toggleSidekickVisibility(isVisible = false)
                }
                // Resets the boolean
                scrollingFromSeekbar = false
            }
        })

        binding.bottomSheetOpener.setOnClickListener {
            toggleSidekickVisibility(isVisible = true)
        }

        binding.seekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // todo: Show thumbnail on top of the seekbar button?!
                if (fromUser) {
                    syncPageNumber(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                scrollingFromSeekbar = true
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // User has stopped moving. Move to the image selected
                val selectedPosition = seekBar?.progress ?: return
                linearLayoutManager.scrollToPosition(selectedPosition)
            }
        })

        viewModel.imageList().observe(this) {
            when (it) {
                is State.Success -> {
                    readerAdapter.submitList(it.value)
                    binding.seekbar.max = it.value.size - 1 // Change it to zero-based integer
                }
            }
        }
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

    /**
     * Syncs current visible position from RecyclerView with the page number shown in the bottom right & seekbar.
     * Should be called in [onScroll]
     * 
     * @param position must be a zero-based integer
     */
    private fun syncPageNumber(position: Int) {
        val itemCount = readerAdapter.itemCount
        val currentVisiblePosition = position + 1

        val pageNumber = "${currentVisiblePosition} / ${itemCount}"
        binding.txtSeekbarCurrentPage.text = pageNumber
        binding.txtCurrentPageMini.text = pageNumber
        binding.seekbar.progress = position
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
        const val CHAPTER_TITLE = "chapter_title"
        const val CHAPTER_URL = "chapter_url"
    }
}
