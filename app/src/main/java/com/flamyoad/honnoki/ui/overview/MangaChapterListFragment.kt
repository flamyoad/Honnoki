package com.flamyoad.honnoki.ui.overview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.flamyoad.honnoki.R

class MangaChapterListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manga_chapter_list, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MangaChapterListFragment()
    }
}
