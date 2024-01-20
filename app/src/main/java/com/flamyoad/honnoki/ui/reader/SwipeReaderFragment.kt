package com.flamyoad.honnoki.ui.reader

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
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

                if (position == imageAdapter.itemCount - 1) {
                    parentViewModel.loadNextChapter()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
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

        binding.smartRefreshLayout.setOnRefreshListener {
            parentViewModel.loadPreviousChapter()
            it.finishRefresh(true)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun scrollTo(pageNumber: Int, chapterId: Long) {
        val adapterItems = imageAdapter.pageList

        val pagePositionInList =
            adapterItems.indexOfFirst {
                if (it is ReaderPage.Value)
                    it.chapter.id == chapterId && it.page.number == pageNumber
                else {
                    false
                }
            }
        binding.viewPager.setCurrentItem(pagePositionInList, false)
    }

    override fun onPagesLoaded(pages: List<ReaderPage>) {
        parentViewModel.currentScrollPosition
        imageAdapter.setList(pages)
    }

    override fun onLoadingPreviousChapter(isLoading: Boolean) {}

    override fun onFailedToLoadPreviousChapter(hasFailed: Boolean) {}

    override fun onLoadingNextChapter(isLoading: Boolean) {
        if (isLoading) {
            binding.topIndicator.showLoading()
        } else {
            binding.topIndicator.hide()
        }
    }

    override fun onFailedToLoadNextChapter(hasFailed: Boolean) {
        binding.topIndicator.showError { parentViewModel.loadNextChapter() }
    }

    override fun onSeekbarPositionChanged(
        position: Int,
        currentChapter: Chapter
    ) {
        val adapterItems = imageAdapter.pageList

        val pagePositionInList =
            adapterItems.indexOfFirst {
                if (it is ReaderPage.Value)
                    it.chapter == currentChapter && it.page.number == position
                else {
                    false
                }
            }
        binding.viewPager.setCurrentItem(pagePositionInList, false)
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

    override fun getInitialChapterId(): Long {
        return arguments?.getLong(CHAPTER_ID) ?: -1
    }

    override fun getOverviewId(): Long {
        return arguments?.getLong(OVERVIEW_ID) ?: -1
    }

    companion object {
        const val TAG = "Vertical Swipe Reader Fragment"

        private const val SWIPE_DIRECTION = "swipe_direction"
        private const val CHAPTER_ID = "chapter_id"
        private const val OVERVIEW_ID = "overview_id"

        @JvmStatic
        fun newInstance(
            swipeDirection: SwipeDirection,
            overviewId: Long,
            chapterId: Long
        ): SwipeReaderFragment {
            return SwipeReaderFragment().apply {
                arguments = bundleOf(
                    SWIPE_DIRECTION to swipeDirection,
                    OVERVIEW_ID to overviewId,
                    CHAPTER_ID to chapterId
                )
            }
        }
    }
}