package com.flamyoad.honnoki.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.paging.ExperimentalPagingApi
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.flamyoad.honnoki.ui.overview.MangaChapterListFragment
import com.flamyoad.honnoki.ui.overview.MangaOverviewActivity
import com.flamyoad.honnoki.ui.overview.MangaSummaryFragment
import java.lang.IllegalArgumentException

class MangaOverviewFragmentAdapter(private val list: List<String>, fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return list.size
    }

    @ExperimentalPagingApi
    override fun createFragment(position: Int): Fragment {
        return when (list[position]) {
            MangaOverviewActivity.TAB_NAME_SUMMARY -> MangaSummaryFragment.newInstance()
            MangaOverviewActivity.TAB_NAME_CHAPTERS -> MangaChapterListFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid tab?")
        }
    }
}