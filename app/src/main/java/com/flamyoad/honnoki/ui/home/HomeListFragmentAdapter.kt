package com.flamyoad.honnoki.ui.home

import androidx.fragment.app.Fragment
import androidx.paging.ExperimentalPagingApi
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.flamyoad.honnoki.model.MangaType
import com.flamyoad.honnoki.model.TabType

class HomeListFragmentAdapter(private val list: List<TabType>, val fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    val childFragmentManager get() = fragment.childFragmentManager

    override fun getItemCount(): Int {
        return 3
    }

    @ExperimentalPagingApi
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DetailedMangaListFragment.newInstance(list[position])
            1 -> SimpleMangaListFragment.newInstance(MangaType.TRENDING)
            2 -> SimpleMangaListFragment.newInstance(MangaType.NEW)
            else -> throw IllegalArgumentException("Invalid index")
        }
    }

}