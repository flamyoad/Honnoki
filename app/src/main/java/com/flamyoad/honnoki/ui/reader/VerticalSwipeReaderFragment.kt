package com.flamyoad.honnoki.ui.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.paging.ExperimentalPagingApi
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.flamyoad.honnoki.data.entities.Chapter
import com.flamyoad.honnoki.databinding.FragmentVerticalSwipeReaderBinding
import com.flamyoad.honnoki.ui.reader.adapter.VerticalSwipeImageAdapter
import com.flamyoad.honnoki.ui.reader.model.ReaderPage

@ExperimentalPagingApi
class VerticalSwipeReaderFragment : BaseReaderFragment() {

    private var _binding: FragmentVerticalSwipeReaderBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val imageAdapter by lazy {
        VerticalSwipeImageAdapter(
            parentViewModel.source,
            parentViewModel.mangadexQualityMode,
            this
        )
    }

    private val viewPagerCallback =
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(
                    position,
                    positionOffset,
                    positionOffsetPixels
                )
                val currentPage = imageAdapter.pageList[position]
                onPageScroll(currentPage, position)
            }

            // For page indicator
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val currentPage = imageAdapter.pageList[position]
                onPageScroll(currentPage, position)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerticalSwipeReaderBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.viewPager) {
            adapter = imageAdapter
            orientation = ViewPager2.ORIENTATION_VERTICAL
            registerOnPageChangeCallback(viewPagerCallback)
            offscreenPageLimit = 1
            doOnLayout {
                initializeReader()
            }
        }
    }

    override fun scrollTo(zeroIndexed: Int) {
        binding.viewPager.setCurrentItem(zeroIndexed, false)
    }

    override fun onPagesLoaded(pages: List<ReaderPage>) {
        imageAdapter.setList(pages)
    }

    override fun onLoadingNextChapter(isLoading: Boolean) {

    }

    override fun onFailedToLoadNextChapter(hasFailed: Boolean) {

    }

    override fun onSeekbarPositionChanged(
        position: Int,
        currentChapter: Chapter
    ) {
        binding.viewPager.setCurrentItem(position, false)
    }

    override fun onNextPage() {
        with(binding.viewPager) {
            if (currentItem == parentViewModel.totalPageNumber.value - 1) {
                return
            }
            setCurrentItem(currentItem + 1, false)
        }
    }

    override fun onPrevPage() {
        with(binding.viewPager) {
            if (currentItem == 0) {
                return
            }
            setCurrentItem(currentItem - 1, false)
        }
    }

    override fun scrollByFixedDistance(distance: Int) {
        TODO("Not yet implemented")
    }

    companion object {
        const val TAG = "Vertical Swipe Reader Fragment"

        @JvmStatic
        fun newInstance() = VerticalSwipeReaderFragment()
    }
}