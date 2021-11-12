package com.flamyoad.honnoki.ui.reader

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.paging.ExperimentalPagingApi
import androidx.viewpager2.widget.ViewPager2
import com.flamyoad.honnoki.data.entities.Chapter
import com.flamyoad.honnoki.databinding.FragmentSwipeReaderBinding
import com.flamyoad.honnoki.ui.reader.adapter.SwipeImageAdapter
import com.flamyoad.honnoki.ui.reader.model.ReaderPage
import kotlinx.parcelize.Parcelize

@Parcelize
enum class SwipeDirection : Parcelable {
    HORIZONTAL,
    VERTICAL
}

@ExperimentalPagingApi
class SwipeReaderFragment : BaseReaderFragment() {

    private var _binding: FragmentSwipeReaderBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val imageAdapter by lazy {
        SwipeImageAdapter(
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
        _binding = FragmentSwipeReaderBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPagerOrientation =
            arguments?.getParcelable<SwipeDirection>(SWIPE_DIRECTION)?.let {
                when (it) {
                    SwipeDirection.HORIZONTAL -> ViewPager2.ORIENTATION_HORIZONTAL
                    SwipeDirection.VERTICAL -> ViewPager2.ORIENTATION_VERTICAL
                }
            } ?: ViewPager2.ORIENTATION_VERTICAL

        with(binding.viewPager) {
            adapter = imageAdapter
            orientation = viewPagerOrientation
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

    override fun onLoadingPreviousChapter(isLoading: Boolean) {

    }

    override fun onFailedToLoadPreviousChapter(hasFailed: Boolean) {

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

        private const val SWIPE_DIRECTION = "swipe_direction"

        @JvmStatic
        fun newInstance(swipeDirection: SwipeDirection): SwipeReaderFragment {
            return SwipeReaderFragment().apply {
                arguments = bundleOf(
                    SWIPE_DIRECTION to swipeDirection
                )
            }
        }
    }
}