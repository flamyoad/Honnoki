package com.flamyoad.honnoki.ui.overview

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.doOnLayout
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.adapter.MangaOverviewFragmentAdapter
import com.flamyoad.honnoki.databinding.ActivityMangaOverviewBinding
import com.flamyoad.honnoki.utils.AppBarStateChangeListener
import com.flamyoad.honnoki.utils.extensions.toast
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator

class MangaOverviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMangaOverviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMangaOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root) // R.layout.activity_manga_overview

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""

        setupViewPager()

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
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.activity_manga_overview_menu, menu)
//        return true
//    }

    private fun setupViewPager() {
        val tabList = listOf(TAB_SUMMARY, TAB_CHAPTERS)

        val pagerAdapter = MangaOverviewFragmentAdapter(tabList,this)

        with(binding.viewPager) {
            adapter = pagerAdapter
            setPageTransformer(com.flamyoad.honnoki.utils.DepthPageTransformer())
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position->
            tab.text = tabList[position]
            binding.viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }

    companion object {
        const val TAB_SUMMARY = "Summary"
        const val TAB_CHAPTERS = "Chapters"
    }
}
