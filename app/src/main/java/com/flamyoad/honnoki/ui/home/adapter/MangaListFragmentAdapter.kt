package com.flamyoad.honnoki.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.flamyoad.honnoki.source.BaseSource
import com.flamyoad.honnoki.source.model.TabType
import com.flamyoad.honnoki.ui.home.mangalist.DetailedMangaListFragment
import com.flamyoad.honnoki.ui.home.mangalist.SimpleMangaListFragment
import com.flamyoad.honnoki.ui.home.model.TabItem

class MangaListFragmentAdapter(val fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    private var tabs: List<TabItem> = emptyList()

    override fun getItemCount(): Int = tabs.size

    fun setSource(sourceImpl: BaseSource) {
        tabs = sourceImpl.getAvailableTabs().map {
            TabItem(source = sourceImpl.getSourceType(), type = it)
        }
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        val itemId = tabs[position].hashCode().toLong()
        return itemId
    }

    override fun containsItem(itemId: Long): Boolean {
        val containsItem = tabs.any { it.hashCode().toLong() == itemId }
        return containsItem
    }

    override fun createFragment(position: Int): Fragment {
        val tab = tabs[position]

        return when (tab.type) {
            TabType.MOST_RECENT -> DetailedMangaListFragment.newInstance(tab.source)
            TabType.TRENDING -> SimpleMangaListFragment.newInstance(tab.source, tab.type)
            TabType.NEW -> SimpleMangaListFragment.newInstance(tab.source, tab.type)
            TabType.LATEST -> SimpleMangaListFragment.newInstance(tab.source, tab.type)
        }
    }
}