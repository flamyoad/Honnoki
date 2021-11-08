package com.flamyoad.honnoki.ui.reader.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.flamyoad.honnoki.parser.model.MangadexQualityMode
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.ui.reader.ImageFragment
import com.flamyoad.honnoki.ui.reader.model.ReaderPage

class SwipeImageAdapter(
    private val source: Source,
    private val mangadexImageQuality: MangadexQualityMode,
    fragment: Fragment,
) :
    FragmentStateAdapter(fragment) {

    var pageList: List<ReaderPage> = emptyList()
        private set

    fun setList(imageList: List<ReaderPage>) {
        this.pageList = imageList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = pageList.size

    override fun createFragment(position: Int): Fragment {
        val page = pageList[position]
        return ImageFragment.newInstance(page, source, mangadexImageQuality)
    }
}