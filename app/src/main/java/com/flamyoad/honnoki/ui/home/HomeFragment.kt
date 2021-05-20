package com.flamyoad.honnoki.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.paging.ExperimentalPagingApi

import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.adapter.HomeListFragmentAdapter
import com.flamyoad.honnoki.databinding.FragmentHomeBinding
import com.flamyoad.honnoki.dialog.SourceSwitcherDialog
import com.flamyoad.honnoki.model.MangaType
import com.flamyoad.honnoki.model.Source
import com.flamyoad.honnoki.model.TabType
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy
import com.flamyoad.honnoki.utils.ui.DepthPageTransformer

@ExperimentalPagingApi
class HomeFragment : Fragment(), SourceSwitcherDialog.Listener {
    private val viewModel: HomeViewModel by activityViewModels()
    private val binding by viewLifecycleLazy { FragmentHomeBinding.bind(requireView()) }

    private val tabList = listOf(
        TabType("all", MangaType.RECENTLY),
        TabType("all", MangaType.TRENDING)
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()

        viewModel.shouldShrinkFab().observe(viewLifecycleOwner, Observer {  shouldShrink ->
            Log.d("debugs", "shouldShrinkfab : $shouldShrink")
            when (shouldShrink) {
                true -> binding.fab.shrink()
                false -> binding.fab.extend()
            }
        })

        binding.fab.text = viewModel.getSourceType().title
        binding.fab.setOnClickListener {
            val dialog = SourceSwitcherDialog.newInstance(this@HomeFragment)
            dialog.show(childFragmentManager, SourceSwitcherDialog.NAME)
        }
    }

    private fun setupViewPager() {
        val pagerAdapter = HomeListFragmentAdapter(tabList,this)

        with(binding.viewPager) {
            adapter = pagerAdapter
            setPageTransformer(DepthPageTransformer())
            isUserInputEnabled = false
        }
    }

    override fun onSourceSwitch(source: Source) {
        if (viewModel.getSourceType() == source)
            return

        binding.fab.text = source.title

        viewModel.switchMangaSource(source)
        setupViewPager()

        // Dismiss the dialog
        childFragmentManager.findFragmentByTag(SourceSwitcherDialog.NAME).let {
            if (it is DialogFragment) {
                it.dismiss()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

}
