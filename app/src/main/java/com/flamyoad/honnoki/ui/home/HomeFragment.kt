package com.flamyoad.honnoki.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.BaseFragment
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.FragmentHomeBinding
import com.flamyoad.honnoki.dialog.SourceSwitcherDialog
import com.flamyoad.honnoki.data.model.MangaType
import com.flamyoad.honnoki.data.model.Source
import com.flamyoad.honnoki.data.model.TabType
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy
import com.flamyoad.honnoki.utils.ui.DepthPageTransformer
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.lang.IllegalArgumentException

@ExperimentalPagingApi
class HomeFragment : BaseFragment(), SourceSwitcherDialog.Listener {

    private val viewModel: HomeViewModel by viewModel()

    private val binding by viewLifecycleLazy { FragmentHomeBinding.bind(requireView()) }

    private val tabList = listOf(
        TabType("all", MangaType.RECENTLY),
        TabType("all", MangaType.TRENDING),
        TabType("all", MangaType.NEW)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()

        viewModel.shouldShrinkFab().observe(viewLifecycleOwner, Observer { shouldShrink ->
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
        val pagerAdapter = HomeListFragmentAdapter(tabList, this)

        with(binding.viewPager) {
            adapter = pagerAdapter
            setPageTransformer(DepthPageTransformer())
        }

        TabLayoutMediator(binding.tabLayoutMain, binding.viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Most Recent"
                1 -> "Trending"
                2 -> "New"
                else -> throw IllegalArgumentException("Invalid tab")
            }
        }.attach()
    }

    override fun onSourceSwitch(source: Source) {
        if (viewModel.getSourceType() == source)
            return

        binding.fab.text = source.title

        setupViewPager()

        // Dismiss the dialog
        childFragmentManager.findFragmentByTag(SourceSwitcherDialog.NAME).let {
            if (it is DialogFragment) {
                it.dismiss()
            }
        }
    }

    override fun getTitle(): String {
        return "Home"
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

}
