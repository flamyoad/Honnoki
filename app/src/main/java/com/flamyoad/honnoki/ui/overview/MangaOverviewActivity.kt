package com.flamyoad.honnoki.ui.overview

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.paging.ExperimentalPagingApi
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.adapter.MangaOverviewFragmentAdapter
import com.flamyoad.honnoki.databinding.ActivityMangaOverviewBinding
import com.flamyoad.honnoki.model.MangaOverview
import com.flamyoad.honnoki.model.State
import com.flamyoad.honnoki.utils.ViewUtils
import com.flamyoad.honnoki.utils.ui.AppBarStateChangeListener
import com.flamyoad.honnoki.utils.extensions.toast
import com.flamyoad.honnoki.utils.ui.DepthPageTransformer
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator

@ExperimentalPagingApi
class MangaOverviewActivity : AppCompatActivity() {
    private var _binding: ActivityMangaOverviewBinding? = null
    val binding get() = requireNotNull(_binding)

    private val viewModel: MangaOverviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMangaOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""

        if (savedInstanceState == null) {
            viewModel.initMangaOverview(
                intent.getStringExtra(MANGA_URL) ?: "",
                intent.getStringExtra(MANGA_SOURCE) ?: "",
            )
        }

        setupUi()
        setupViewPager()
    }

    private fun setupUi() {
        binding.appbarLayout.addOnOffsetChangedListener(object: AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                when (state) {
                    State.EXPANDED -> binding.toolbarContent.alpha = 0f
                    State.COLLAPSED -> binding.toolbarContent.alpha = 1f
                    else -> binding.toolbarContent.alpha = 0f
                }
            }
        })

        binding.btnRead.setOnClickListener {
            toast("clicked")
        }

        viewModel.mangaOverview().observe(this) {
            when (it) {
                is State.Success -> { showMangaOverview(it.value) }
//                is State.Error -> { binding.imageMangaLayout.viewState = MultiStateView.ViewState.ERROR }
//                is State.Loading -> { binding.imageMangaLayout.viewState = MultiStateView.ViewState.LOADING }
            }
        }
    }

    private fun setupViewPager() {
        val tabList = listOf(TAB_NAME_SUMMARY, TAB_NAME_CHAPTERS)

        val pagerAdapter = MangaOverviewFragmentAdapter(tabList,this)

        with(binding.viewPager) {
            adapter = pagerAdapter
            setPageTransformer(DepthPageTransformer())
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position->
            tab.text = tabList[position]
            binding.viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }

    private fun showMangaOverview(overview: MangaOverview) {
        with(binding) {
            Glide.with(this@MangaOverviewActivity)
                .load(overview.coverImage)
                .into(imageBackground)

            Glide.with(this@MangaOverviewActivity)
                .load(overview.coverImage)
                .placeholder(ViewUtils.getLoadingIndicator(this@MangaOverviewActivity))
                .error(R.drawable.ic_error_outline_black_24dp)
                .into(imageManga)

            if (overview.alternativeTitle.isBlank()) {
                txtAlternateName.isVisible = false
            } else {
                txtAlternateName.text = overview.alternativeTitle
            }

            txtTitle.text = overview.mainTitle
            txtAlternateName.text = overview.alternativeTitle
            txtAuthor.text = overview.authors.joinToString { it.name }
            txtStatus.text = overview.status
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAB_NAME_SUMMARY = "Summary"
        const val TAB_NAME_CHAPTERS = "Chapters"

        const val MANGA_URL = "manga_url"
        const val MANGA_SOURCE = "manga_source"
        const val MANGA_TITLE = "manga_title"
    }
}
