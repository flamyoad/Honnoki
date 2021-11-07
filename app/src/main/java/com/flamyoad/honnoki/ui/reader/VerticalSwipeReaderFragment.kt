package com.flamyoad.honnoki.ui.reader

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.FragmentVerticalScrollingReaderBinding
import com.flamyoad.honnoki.databinding.FragmentVerticalSwipeReaderBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@ExperimentalPagingApi
class VerticalSwipeReaderFragment : Fragment() {

    private var _binding: FragmentVerticalSwipeReaderBinding? = null
    private val binding = requireNotNull(_binding)

    private val parentViewModel: ReaderViewModel by sharedViewModel()

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

    companion object {
        const val TAG = "Vertical Swipe Reader Fragment"

        @JvmStatic
        fun newInstance() = VerticalSwipeReaderFragment()
    }
}