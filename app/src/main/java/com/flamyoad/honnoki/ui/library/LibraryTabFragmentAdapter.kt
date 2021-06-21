package com.flamyoad.honnoki.ui.library

import androidx.fragment.app.Fragment
import androidx.paging.ExperimentalPagingApi
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.flamyoad.honnoki.ui.library.bookmark.BookmarkFragment
import com.flamyoad.honnoki.ui.library.downloads.DownloadFragment
import com.flamyoad.honnoki.ui.library.history.ReadHistoryFragment
import java.lang.IllegalArgumentException

class LibraryTabFragmentAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    @ExperimentalPagingApi
    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return BookmarkFragment.newInstance()
            1 -> return ReadHistoryFragment.newInstance()
            2 -> return DownloadFragment.newInstance()
            else -> throw IllegalArgumentException("Tab does not exist!")
        }
    }

}