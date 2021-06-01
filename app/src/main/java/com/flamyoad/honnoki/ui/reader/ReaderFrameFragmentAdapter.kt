package com.flamyoad.honnoki.ui.reader

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.paging.ExperimentalPagingApi
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.flamyoad.honnoki.model.Chapter

@ExperimentalPagingApi
class ReaderFrameFragmentAdapter(activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    private var chapterList: List<Chapter> = emptyList()

    override fun getItemCount(): Int {
        return chapterList.size
    }

    fun setList(chapterList: List<Chapter>) {
        this.chapterList = chapterList
        notifyDataSetChanged()
    }

    override fun createFragment(position: Int): Fragment {
        return ReaderFrameFragment.newInstance(chapterList[position])
    }
}