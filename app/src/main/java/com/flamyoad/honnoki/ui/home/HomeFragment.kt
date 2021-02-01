package com.flamyoad.honnoki.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.adapter.HomeListFragmentAdapter
import com.flamyoad.honnoki.databinding.FragmentHomeBinding
import com.flamyoad.honnoki.model.TabType
import com.flamyoad.honnoki.utils.DepthPageTransformer
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by activityViewModels()
    private val binding by viewLifecycleLazy { FragmentHomeBinding.bind(requireView()) }

    private val tabList = listOf(
        TabType("all", "Most Recent"),
        TabType("all", "Trending")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()

        viewModel.shouldShrinkFab().observe(viewLifecycleOwner, Observer {  shouldShrink ->
            when (shouldShrink) {
                true -> binding.fab.shrink()
                false -> binding.fab.extend()
            }
        })
    }

    fun setupViewPager() {
        val pagerAdapter = HomeListFragmentAdapter(tabList,this)

        with(binding.viewPager) {
            adapter = pagerAdapter
            setPageTransformer(DepthPageTransformer())
        }

        TabLayoutMediator(binding.tabLayoutSub, binding.viewPager) { tab, position->
            tab.text = tabList[position].type
            binding.viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}
