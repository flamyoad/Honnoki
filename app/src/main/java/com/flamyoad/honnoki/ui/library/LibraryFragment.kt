package com.flamyoad.honnoki.ui.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.flamyoad.honnoki.BaseFragment

import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.adapter.LibraryTabsAdapter
import com.flamyoad.honnoki.databinding.FragmentLibraryBinding
import com.flamyoad.honnoki.utils.ui.DepthPageTransformer
import com.google.android.material.tabs.TabLayoutMediator
import java.lang.IllegalArgumentException

class LibraryFragment : BaseFragment() {

    private var _binding: FragmentLibraryBinding? = null
    val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLibraryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabAdapter = LibraryTabsAdapter(this)

        with(binding.viewPager) {
            adapter = tabAdapter
            setPageTransformer(DepthPageTransformer())
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Bookmarks"
                1 -> "Read History"
                2 -> "Downloads"
                else -> throw IllegalArgumentException("Invalid tab")
            }
        }.attach()
    }

    override fun getTitle(): String {
        return "Library"
    }

    companion object {
        @JvmStatic
        fun newInstance() = LibraryFragment()
    }
}
