package com.flamyoad.honnoki.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.paging.ExperimentalPagingApi
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.flamyoad.honnoki.data.entities.MangaType
import com.flamyoad.honnoki.data.entities.Source
import com.flamyoad.honnoki.ui.home.mangalist.DetailedMangaListFragment
import com.flamyoad.honnoki.ui.home.mangalist.SimpleMangaListFragment
import com.flamyoad.honnoki.ui.home.model.TabItem

class MangaListFragmentAdapter(val fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    private var items = listOf<TabItem>()

    override fun getItemCount(): Int = items.size

    fun setSource(source: Source) {
        val newItems = mutableListOf<TabItem>()
        for (i in 0 until 3) {
            val type = when (i) {
                0 -> MangaType.RECENTLY
                1 -> MangaType.TRENDING
                2 -> MangaType.NEW
                else -> throw IllegalArgumentException()
            }
            newItems.add(TabItem(source, type))
        }
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        val itemId = items[position].hashCode().toLong()
        return itemId
    }

    override fun containsItem(itemId: Long): Boolean {
        val containsItem = items.any { it.hashCode().toLong() == itemId }
        return containsItem
    }

    @ExperimentalPagingApi
    override fun createFragment(position: Int): Fragment {
        val mangaSource = items[position].source
        return when (position) {
            0 -> DetailedMangaListFragment.newInstance(mangaSource)
            1 -> SimpleMangaListFragment.newInstance(mangaSource, MangaType.TRENDING)
            2 -> SimpleMangaListFragment.newInstance(mangaSource, MangaType.NEW)
            else -> throw IllegalArgumentException("Invalid index")
        }
    }
}