package com.flamyoad.honnoki.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.flamyoad.honnoki.MainViewModel

import com.flamyoad.honnoki.databinding.FragmentLibraryBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.lang.IllegalArgumentException

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    val binding get() = requireNotNull(_binding)

    private val mainViewModel: MainViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        val tabAdapter = LibraryTabFragmentAdapter(this)

        with(binding.viewPager) {
            adapter = tabAdapter
            setPageTransformer(DepthPageTransformer())
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Bookmarks"
                1 -> "Read History"
                2 -> "Downloads"
                else -> throw IllegalArgumentException("Invalid tab")
            }
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                mainViewModel.setActionMode(false)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = LibraryFragment()
    }
}
