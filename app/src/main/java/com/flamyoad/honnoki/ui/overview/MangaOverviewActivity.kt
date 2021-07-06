package com.flamyoad.honnoki.ui.overview

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.ContextMenu
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
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.cache.CacheManager
import com.flamyoad.honnoki.data.entities.Author
import com.flamyoad.honnoki.data.entities.MangaOverview
import com.flamyoad.honnoki.databinding.ActivityMangaOverviewBinding
import com.flamyoad.honnoki.dialog.BookmarkDialog
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.ui.lookup.MangaLookupActivity
import com.flamyoad.honnoki.ui.lookup.model.LookupType
import com.flamyoad.honnoki.ui.overview.adapter.MangaOverviewFragmentAdapter
import com.flamyoad.honnoki.ui.reader.ReaderActivity
import com.flamyoad.honnoki.utils.ui.AppBarStateChangeListener
import com.flamyoad.honnoki.utils.ui.DepthPageTransformer
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kennyc.view.MultiStateView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class MangaOverviewActivity : AppCompatActivity() {
    private var _binding: ActivityMangaOverviewBinding? = null
    val binding get() = requireNotNull(_binding)

    private val mangaSource: String by lazy { intent.getStringExtra(MANGA_SOURCE) ?: "" }

    private val viewModel: MangaOverviewViewModel by viewModel {
        parametersOf(mangaSource)
    }

    private val cacheManager: CacheManager by inject()

    private var ioJob: Job? = null

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
        }

        if (savedInstanceState == null) {
            viewModel.loadMangaOverview(intent.getStringExtra(MANGA_URL) ?: "")
        }

        initUi()
        initViewPager()
        observeUi()
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

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.apply {
            setHeaderTitle("More options")
            add(OPEN_IN_BROWSER)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.title) {
            OPEN_IN_BROWSER -> {
                viewModel.overview.let {
                    if (it == MangaOverview.empty()) {
                        return true
                    }
                    openBrowser(it)
                }
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    private fun initUi() {
        binding.appbarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                with(binding) {
                    when (state) {
                        State.EXPANDED -> {
                            toolbarContent.alpha = 0f
                            showToolbarArea(ToolbarState.EXPANDED)
                            swipeRefreshLayout.isEnabled = true
                        }
                        State.COLLAPSED -> {
                            toolbarContent.alpha = 1f
                            showToolbarArea(ToolbarState.COLLAPSED)
                            swipeRefreshLayout.isEnabled = false
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

        registerForContextMenu(binding.btnMore)
        binding.btnMore.setOnClickListener {
            it.showContextMenu()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadMangaOverview(intent.getStringExtra(MANGA_URL) ?: "")
            binding.swipeRefreshLayout.isRefreshing = false
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

        var start = 0
        var end = 0

        for (author in authors) {
            start = authorText.indexOf(author.name, start)
            end = start + (author.name.length)
            authorText[start..end] = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    lookupMangaByAuthor(author)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.WHITE
                }
            }
            // Fix issue when two author share the same value
            // i.e. Azuki, Azuki
            start = end
        }

        with(binding.txtAuthor) {
            text = authorText
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun showMangaOverview(overview: MangaOverview) {
        if (overview == MangaOverview.empty()) return

        with(binding) {
            if (overview.alternativeTitle.isBlank()) {
                txtAlternateName.isVisible = false
            } else {
                txtAlternateName.text = overview.alternativeTitle
            }

            txtTitle.text = overview.mainTitle
            txtAlternateName.text = overview.alternativeTitle
            txtStatus.text = overview.status
        }

        cacheManager.getCoverImage(this, overview.coverImage) {
            loadImage(it)
        }
    }

    private fun loadImage(file: File) {
        Glide.with(this@MangaOverviewActivity)
            .load(file)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.imageMangaLayout.viewState = MultiStateView.ViewState.CONTENT
                    return false
                }
            })
            .into(binding.imageManga)

        Glide.with(this@MangaOverviewActivity)
            .load(file)
            .into(binding.imageBackground)
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

    private fun lookupMangaByAuthor(author: Author) {
        val source = Source.valueOf(mangaSource)
        MangaLookupActivity.startActivity(this, author.link, author.name, source, LookupType.AUTHOR)
    }

    private fun openBrowser(overview: MangaOverview) {
        val uri = if (overview.source == Source.MANGADEX) {
            Uri.parse("https://mangadex.org/title/{${overview.link}}")
        } else {
            Uri.parse(overview.link)
        }
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun startReading() {
        if (ioJob?.isActive == true) return

        val overview = viewModel.overview
        val overviewId = overview.id ?: return

        ioJob = lifecycleScope.launch(Dispatchers.IO) {
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

        // Context menus
        const val OPEN_IN_BROWSER = "Open in browser"

        // For restoring state across process death
        const val OVERVIEW_URL = "overview_url"

        // For intents
        const val MANGA_URL = "manga_url"
        const val MANGA_SOURCE = "manga_source"
        const val MANGA_TITLE = "manga_title"

        fun startActivity(
            context: Context,
            mangaUrl: String,
            mangaSource: Source,
            mangaTitle: String
        ) {
            val intent = Intent(context, MangaOverviewActivity::class.java)
            intent.apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(MANGA_URL, mangaUrl)
                putExtra(MANGA_SOURCE, mangaSource.toString())
                putExtra(MANGA_TITLE, mangaTitle)
            }
            context.startActivity(intent)
        }
    }
}
