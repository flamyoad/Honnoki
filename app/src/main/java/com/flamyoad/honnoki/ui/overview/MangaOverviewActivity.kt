package com.flamyoad.honnoki.ui.overview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.adapter.HomeListFragmentAdapter
import com.flamyoad.honnoki.adapter.MangaOverviewFragmentAdapter
import com.flamyoad.honnoki.databinding.ActivityMainBinding
import com.flamyoad.honnoki.databinding.ActivityMangaOverviewBinding
import com.google.android.material.tabs.TabLayoutMediator

class MangaOverviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMangaOverviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMangaOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root) // R.layout.activity_manga_overview

        setupViewPager()
    }

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
