package com.flamyoad.honnoki.ui.lookup

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.data.entities.LookupResult
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.databinding.ActivityMangaLookupBinding
import com.flamyoad.honnoki.ui.lookup.adapter.MangaLookupAdapter
import com.flamyoad.honnoki.ui.lookup.model.LookupType
import com.flamyoad.honnoki.ui.overview.MangaOverviewActivity
import com.flamyoad.honnoki.ui.search.adapter.SearchResultEndOfListAdapter
import com.flamyoad.honnoki.utils.extensions.findViewFromError
import com.flamyoad.honnoki.utils.extensions.getInteger
import com.kennyc.view.MultiStateView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MangaLookupActivity : AppCompatActivity() {

    private var _binding: ActivityMangaLookupBinding? = null
    private val binding get() = requireNotNull(_binding)

    /**
     * Parameter of the API. It could be an API parameter or full website URL depending on the source
     */
    private val params: String by lazy {
        intent.getStringExtra(API_PARAMS) ?: ""
    }

    /**
     * Value could be author's name or genre's name depending on the lookup type.
     * Basically it's just a dumb string being shown in the toolbar and it is not being used
     * in ViewModel
     */
    private val paramsName: String by lazy {
        intent.getStringExtra(PARAMS_NAME) ?: ""
    }

    /**
     * String value of the Source enum. Used as constructor parameter when injecting ViewModel
     */
    private val sourceName: String by lazy {
        intent.getStringExtra(SOURCE_NAME) ?: ""
    }

    /**
     * Lookup types include Genre, Author. Future possibilities might include Groups etc.
     */
    private val lookupType: LookupType by lazy {
        val value = intent.getStringExtra(LOOKUP_TYPE) ?: ""
        LookupType.valueOf(value)
    }

    private val viewModel: MangaLookupViewModel by viewModel {
        parametersOf(params, sourceName, lookupType)
    }

    private val lookupAdapter by lazy { MangaLookupAdapter(this::openManga) }
    private val searchResultEndOfListAdapter by lazy { SearchResultEndOfListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMangaLookupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.listManga.isVisible = false

        initUi()
        initRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_manga_lookup_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    private fun initUi() {
        with(binding) {
            txtToolbarPrimary.text = "${lookupType.readableName}: $paramsName"
            txtToolbarSecondary.text = Source.valueOf(sourceName).title

            binding.multiStateView.findViewFromError(R.id.btnRetry)?.let {
                it.setOnClickListener { lookupAdapter.retry() }
            }
        }
    }

    private fun initRecyclerView() {
        val gridLayoutManager = GridLayoutManager(
            this, getInteger(R.integer.manga_grid_spancount)
        )
        with(binding.listManga) {
            adapter = lookupAdapter
            layoutManager = gridLayoutManager
        }

        lifecycleScope.launchWhenResumed {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.lookupResult.collectLatest {
                        lookupAdapter.submitData(it)
                    }
                }
            }
        }

        lookupAdapter.addLoadStateListener {
            when (it.mediator?.refresh) {
                is LoadState.Error -> binding.multiStateView.viewState =
                    MultiStateView.ViewState.ERROR
                is LoadState.Loading -> binding.multiStateView.viewState =
                    MultiStateView.ViewState.LOADING
                is LoadState.NotLoading -> binding.multiStateView.viewState =
                    MultiStateView.ViewState.CONTENT
                else -> {}
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun openManga(lookupResult: LookupResult) {
        val intent = Intent(this, MangaOverviewActivity::class.java).apply {
            putExtra(MangaOverviewActivity.MANGA_URL, lookupResult.link)
            putExtra(MangaOverviewActivity.MANGA_SOURCE, sourceName)
            putExtra(MangaOverviewActivity.MANGA_TITLE, lookupResult.title)
        }
        startActivity(intent)
    }

    companion object {
        const val API_PARAMS = "api_params"
        const val PARAMS_NAME = "params_name"
        const val SOURCE_NAME = "source_name"
        const val LOOKUP_TYPE = "lookup_type"

        /**
         * The currently running instance of activity B in the above example will either receive
         * the new intent you are starting here in its onNewIntent() method, or be itself finished
         * and restarted with the new intent. If it has declared its launch mode to be "multiple"
         * (the default) and you have not set FLAG_ACTIVITY_SINGLE_TOP in the same intent, then
         * it will be finished and re-created; for all other launch modes or if FLAG_ACTIVITY_SINGLE_TOP
         * is set then this Intent will be delivered to the current instance's onNewIntent().
         *
         * If (Intent.FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_SINGLE_TOP) is used,
         * The activity will not be recreated and only onNewIntent() will be called
         */
        fun startActivity(
            context: Context,
            params: String,
            name: String,
            source: Source,
            lookupType: LookupType
        ) {
            val intent = Intent(context, MangaLookupActivity::class.java)
            intent.apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(API_PARAMS, params)
                putExtra(PARAMS_NAME, name)
                putExtra(SOURCE_NAME, source.name)
                putExtra(LOOKUP_TYPE, lookupType.name)
            }
            context.startActivity(intent)
        }
    }
}