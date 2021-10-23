package com.flamyoad.honnoki.ui.download

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.cache.CacheManager
import com.flamyoad.honnoki.common.State
import com.flamyoad.honnoki.databinding.ActivityDownloadPickerBinding
import com.flamyoad.honnoki.ui.download.adapter.DownloadActionItemsAdapter
import com.flamyoad.honnoki.ui.download.adapter.DownloadChapterGridAdapter
import com.flamyoad.honnoki.ui.overview.adapter.LanguageFilterAdapter
import com.flamyoad.honnoki.ui.overview.model.LanguageFilter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class DownloadPickerActivity : AppCompatActivity() {

    private val viewModel: DownloadPickerViewModel by viewModel()

    private var _binding: ActivityDownloadPickerBinding? = null
    val binding get() = requireNotNull(_binding)

    private val cacheManager: CacheManager by inject()

    private val overviewId by lazy { intent.getLongExtra(OVERVIEW_ID, -1L) }
    private val mangaTitle by lazy { intent.getStringExtra(TITLE) }
    private val coverImageUrl by lazy { intent.getStringExtra(COVER_IMAGE) }
    private val languageLocale by lazy { intent.getStringExtra(LANGUAGE_LOCALE) }

    private val actionItemsAdapter by lazy {
        DownloadActionItemsAdapter(
            viewModel::toggleChapterListSort,
            viewModel::selectAllChapters,
            viewModel::unselectAllChapters,
        )
    }
    private val languageFilterAdapter by lazy {
        LanguageFilterAdapter(this) {
            viewModel.selectLanguageLocale(it.locale)
        }
    }

    private val concatAdapter by lazy { ConcatAdapter(actionItemsAdapter) }

    private val chapterListAdapter by lazy {
        DownloadChapterGridAdapter(viewModel::onChapterPress, viewModel::onChapterLongPress)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDownloadPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = mangaTitle

        initUi()
        observeUi()
        viewModel.initChapterList(overviewId)

        if (savedInstanceState == null) {
            viewModel.selectLanguageLocale(languageLocale ?: "")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initUi() {
        cacheManager.getCoverImage(this, coverImageUrl ?: return) {
            Glide.with(this)
                .load(it)
                .into(binding.coverImage)
        }

        val gridLayoutManager = GridLayoutManager(this, 3)
        with(binding.listChapter) {
            adapter = chapterListAdapter
            layoutManager = gridLayoutManager
        }

        val linearLayoutManager = LinearLayoutManager(this)
        with(binding.listOptions) {
            adapter = concatAdapter
            layoutManager = linearLayoutManager
        }

        if (languageLocale.isNullOrBlank().not()) {
            concatAdapter.addAdapter(languageFilterAdapter)
        }

        actionItemsAdapter.setItem(0)

        binding.btnDownload.setOnClickListener {
            viewModel.downloadChapters()
            finish()
        }
    }


    private fun observeUi() {
        lifecycleScope.launch {
            viewModel.chapterList.collectLatest {
                if (it is State.Success) {
                    chapterListAdapter.submitList(it.value)
                    actionItemsAdapter.setItem(it.value.size)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.selectedChapters().collectLatest {
                with(binding.btnDownload) {
                    text = if (it.isEmpty()) {
                        resources.getString(R.string.download_list_no_items)
                    } else {
                        resources.getQuantityString(R.plurals.download_list_total, it.size, it.size)
                    }
                    isEnabled = it.isNotEmpty()
                }
            }
        }

        viewModel.languageList.observe(this) {
            languageFilterAdapter.items = it
        }
    }

    companion object {
        private const val OVERVIEW_ID = "overview_id"
        private const val TITLE = "title"
        private const val COVER_IMAGE = "cover_image"
        private const val LANGUAGE_LOCALE = "language_locale"

        fun startActivity(
            context: Context,
            overviewId: Long,
            title: String,
            coverImageUrl: String,
            languageLocale: LanguageFilter,
        ) {
            val intent = Intent(context, DownloadPickerActivity::class.java).apply {
                putExtra(OVERVIEW_ID, overviewId)
                putExtra(TITLE, title)
                putExtra(COVER_IMAGE, coverImageUrl)
                putExtra(LANGUAGE_LOCALE, languageLocale.locale)
            }
            context.startActivity(intent)
        }
    }
}