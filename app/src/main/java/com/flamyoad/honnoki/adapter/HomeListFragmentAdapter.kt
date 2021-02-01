package com.flamyoad.honnoki.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.flamyoad.honnoki.model.TabType
import com.flamyoad.honnoki.ui.home.HomeListFragment

class HomeListFragmentAdapter(private val list: List<TabType>, fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return HomeListFragment.newInstance(list[position])
    }

}