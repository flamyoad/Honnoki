package com.flamyoad.honnoki.ui.lookup

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.GridLayoutManager
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.data.Source
import com.flamyoad.honnoki.data.entities.SearchResult
import com.flamyoad.honnoki.databinding.ActivityMangaLookupBinding
import com.flamyoad.honnoki.di.viewModelModules
import com.flamyoad.honnoki.ui.lookup.adapter.MangaLookupAdapter
import com.flamyoad.honnoki.ui.lookup.model.LookupType
import com.flamyoad.honnoki.ui.overview.MangaOverviewActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class MangaLookupActivity : AppCompatActivity() {

    private var _binding: ActivityMangaLookupBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val params: String by lazy { intent.getStringExtra(API_PARAMS) ?: "" }
    private val name: String by lazy { intent.getStringExtra(PARAMS_NAME) ?: "" }
    private val sourceName: String by lazy { intent.getStringExtra(SOURCE_NAME) ?: "" }

    private val lookupType: LookupType by lazy {
        val value = intent.getStringExtra(LOOKUP_TYPE) ?: ""
        LookupType.valueOf(value)
    }

    private val viewModel: MangaLookupViewModel by viewModel {
        parametersOf(params, sourceName, lookupType)
    }

    private val lookupAdapter by lazy { MangaLookupAdapter(this::openManga) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMangaLookupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        initUi()
        initRecyclerView()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        unloadKoinModules(viewModelModules)
        loadKoinModules(viewModelModules)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_manga_lookup_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun initUi() {
        with(binding) {
            txtToolbarPrimary.text = "${lookupType.readableName}: $name"
            txtToolbarSecondary.text = Source.valueOf(sourceName).title
        }
    }

    private fun initRecyclerView() {
        val gridLayoutManager = GridLayoutManager(this, 3)
        with(binding.listManga) {
            adapter = lookupAdapter
            layoutManager = gridLayoutManager
        }

        lifecycleScope.launchWhenResumed {
            viewModel.lookupResult.collectLatest {
                lookupAdapter.submitData(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun openManga(searchResult: SearchResult) {
        val intent = Intent(this, MangaOverviewActivity::class.java).apply {
            putExtra(MangaOverviewActivity.MANGA_URL, searchResult.link)
            putExtra(MangaOverviewActivity.MANGA_SOURCE, sourceName)
            putExtra(MangaOverviewActivity.MANGA_TITLE, searchResult.title)
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