package com.flamyoad.honnoki.ui.overview

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.data.entities.Author
import com.flamyoad.honnoki.ui.overview.adapter.MangaOverviewFragmentAdapter
import com.flamyoad.honnoki.databinding.ActivityMangaOverviewBinding
import com.flamyoad.honnoki.dialog.BookmarkDialog
import com.flamyoad.honnoki.data.entities.MangaOverview
import com.flamyoad.honnoki.ui.reader.ReaderActivity
import com.flamyoad.honnoki.utils.ViewUtils
import com.flamyoad.honnoki.utils.extensions.toast
import com.flamyoad.honnoki.utils.ui.AppBarStateChangeListener
import com.flamyoad.honnoki.utils.ui.DepthPageTransformer
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class MangaOverviewActivity : AppCompatActivity() {
    private var _binding: ActivityMangaOverviewBinding? = null
    val binding get() = requireNotNull(_binding)

    private val mangaSource: String by lazy {
        intent.getStringExtra(MANGA_SOURCE) ?: ""
    }

    private val viewModel: MangaOverviewViewModel by viewModel {
        parametersOf(mangaSource)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMangaOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""

        // Transparent status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                window.navigationBarColor = getColor(R.color.sea_blue)
            }
        } else {
            binding.appbarLayout.updatePadding(top = 32)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        if (savedInstanceState == null) {
            viewModel.loadMangaOverview(intent.getStringExtra(MANGA_URL) ?: "")
        }

        initUi()
        initViewPager()
        observeUi()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(OVERVIEW_URL, viewModel.overview.link)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.getString(OVERVIEW_URL)?.let {
            viewModel.loadMangaOverview(it)
        }
    }

    private fun initUi() {
        binding.appbarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                with(binding) {
                    when (state) {
                        State.EXPANDED -> {
                            toolbarContent.alpha = 0f
                            showToolbarArea(ToolbarState.EXPANDED)
                            swipeRefreshLayout.isEnabled = false
                        }
                        State.COLLAPSED -> {
                            toolbarContent.alpha = 1f
                            showToolbarArea(ToolbarState.COLLAPSED)
                            swipeRefreshLayout.isEnabled = true
                        }
                        State.IDLE -> {
                        }
                    }
                }
            }

            /**
             * @param i Vertical offset of the AppBarLayout. When fully expanded, it shows 0
             * When not fully expanded, it shows values in the range [-∞,-1]
             */
            override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
                super.onOffsetChanged(appBarLayout, i)
                /**
                 * The condition below is to know when the scrolled distance falls between EXPANDED and COLLAPSED
                 *
                 * onOffsetChanged() returns negative value on scrolling.
                 * totalScrollRange() returns positive value. Therefore we have to convert it to negative value
                 */
                val totalScrollRange = appBarLayout.totalScrollRange.unaryMinus()
                if ((i < 0) && (i > totalScrollRange)) {
                    showToolbarArea(ToolbarState.EXPANDED)
                    binding.swipeRefreshLayout.isEnabled = false
                }

            }
        })

        binding.btnFavouriteExpanded.setOnClickListener {
            showBookmarkGroupDialog()
        }

        binding.btnFavouriteCollapsed.setOnClickListener {
            showBookmarkGroupDialog()
        }

        binding.btnReadCollapsed.setOnClickListener {
            startReading()
        }

        binding.btnReadExpanded.setOnClickListener {
            startReading()
        }
    }

    private fun initViewPager() {
        val tabList = listOf(TAB_NAME_SUMMARY)

        val pagerAdapter = MangaOverviewFragmentAdapter(tabList, this)

        with(binding.viewPager) {
            adapter = pagerAdapter
            setPageTransformer(DepthPageTransformer())
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabList[position]
            binding.viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }

    private fun observeUi() {
        viewModel.hasBeenBookmarked.observe(this) {
            binding.btnFavouriteCollapsed.isChecked = it
            binding.btnFavouriteExpanded.isChecked = it
        }

        viewModel.authorList.observe(this) { authors ->
            buildAuthorSpannableString(authors)
        }

        lifecycleScope.launchWhenResumed {
            viewModel.mangaOverview.collectLatest {
                showMangaOverview(it)
            }
        }
    }

    private fun buildAuthorSpannableString(authors: List<Author>) {
        val authorText = authors.joinToString { it.name }
            .toSpannable()

        for (author in authors) {
            val start = authorText.indexOf(author.name)
            val end = start + (author.name.length)
            authorText[start..end] = object: ClickableSpan() {
                override fun onClick(widget: View) {
                    toast(author.name)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.WHITE
                }
            }
        }

        with(binding.txtAuthor) {
            text = authorText
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun showMangaOverview(overview: MangaOverview) {
        with(binding) {
            Glide.with(this@MangaOverviewActivity)
                .load(overview.coverImage)
                .timeout(10000)
                .into(imageBackground)

            Glide.with(this@MangaOverviewActivity)
                .load(overview.coverImage)
                .timeout(10000)
                .placeholder(ViewUtils.getLoadingIndicator(this@MangaOverviewActivity))
                .into(imageManga)

            if (overview.alternativeTitle.isBlank()) {
                txtAlternateName.isVisible = false
            } else {
                txtAlternateName.text = overview.alternativeTitle
            }

            txtTitle.text = overview.mainTitle
            txtAlternateName.text = overview.alternativeTitle
            txtStatus.text = overview.status
        }
    }

    private fun showToolbarArea(state: ToolbarState) {
        with(binding) {
            btnReadExpanded.isVisible = state == ToolbarState.EXPANDED
            btnFavouriteExpanded.isVisible = state == ToolbarState.EXPANDED

            btnReadCollapsed.isVisible = state == ToolbarState.COLLAPSED
            btnFavouriteCollapsed.isVisible = state == ToolbarState.COLLAPSED
        }
    }

    private fun showBookmarkGroupDialog() {
        val overviewId = viewModel.overview.id ?: return
        if (overviewId == -1L) return

        val dialog = BookmarkDialog.newInstance(overviewId)
        dialog.show(supportFragmentManager, "bookmark_dialog")
    }

    private fun startReading() {
        val overview = viewModel.overview
        val overviewId = overview.id ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val chapterId = if (overview.lastReadChapterId == -1L) {
                viewModel.getFirstChapter(overviewId)?.id
            } else {
                overview.lastReadChapterId
            }

            if (chapterId == null) return@launch
            if (overview.source == null) return@launch

            val startAtPage = if (overview.lastReadChapterId == chapterId) {
                overview.lastReadPageNumber
            } else {
                0
            }
            ReaderActivity.start(
                this@MangaOverviewActivity,
                chapterId,
                overviewId,
                startAtPage,
                overview.source
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAB_NAME_SUMMARY = "Summary"
        const val TAB_NAME_CHAPTERS = "Chapters"

        // For restoring state across process death
        const val OVERVIEW_URL = "overview_url"

        // For intents
        const val MANGA_URL = "manga_url"
        const val MANGA_SOURCE = "manga_source"
        const val MANGA_TITLE = "manga_title"
    }
}
