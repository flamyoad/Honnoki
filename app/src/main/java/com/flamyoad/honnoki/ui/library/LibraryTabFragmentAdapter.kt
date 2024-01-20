package com.flamyoad.honnoki.ui.library

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.flamyoad.honnoki.ui.library.bookmark.BookmarkFragment
import com.flamyoad.honnoki.ui.library.downloads.DownloadFragment
import com.flamyoad.honnoki.ui.library.history.ReadHistoryFragment
import java.lang.IllegalArgumentException

class LibraryTabFragmentAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BookmarkFragment.newInstance()
            1 -> ReadHistoryFragment.newInstance()
    //            2 -> return DownloadFragment.newInstance()
            else -> throw IllegalArgumentException("Tab does not exist!")
        }
    }

}