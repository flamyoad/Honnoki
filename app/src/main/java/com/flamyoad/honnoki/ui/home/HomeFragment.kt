package com.flamyoad.honnoki.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.BaseFragment
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.FragmentHomeBinding
import com.flamyoad.honnoki.dialog.SourceSwitcherDialog
import com.flamyoad.honnoki.data.Source
import com.flamyoad.honnoki.ui.home.adapter.MangaListFragmentAdapter
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy
import com.flamyoad.honnoki.utils.ui.DepthPageTransformer
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.lang.IllegalArgumentException

@ExperimentalPagingApi
class HomeFragment : BaseFragment(), SourceSwitcherDialog.Listener {
    private val binding by viewLifecycleLazy { FragmentHomeBinding.bind(requireView()) }

    private val viewModel: HomeViewModel by sharedViewModel()

    private val viewPagerAdapter by lazy { MangaListFragmentAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        setupViewPager()

        lifecycleScope.launchWhenResumed {
            viewModel.chosenSource.collectLatest {  source ->
                viewPagerAdapter.setSource(source)
            }
        }
    }

    private fun initUi() {
        viewModel.shouldShrinkFab().observe(viewLifecycleOwner) { shouldShrink ->
            when (shouldShrink) {
                true -> binding.fab.shrink()
                false -> binding.fab.extend()
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.chosenSource.collectLatest {
                binding.fab.text = it.title
            }
        }

        binding.fab.setOnClickListener {
            val dialog = SourceSwitcherDialog.newInstance(this@HomeFragment)
            dialog.show(childFragmentManager, SourceSwitcherDialog.NAME)
        }
    }

    private fun setupViewPager() {
        with(binding.viewPager) {
            adapter = viewPagerAdapter
            setPageTransformer(DepthPageTransformer())
        }

        TabLayoutMediator(binding.tabLayoutMain, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Most Recent"
                1 -> "Trending"
                2 -> "New"
                else -> throw IllegalArgumentException("Invalid tab")
            }
        }.attach()
    }

    override fun onSourceSwitch(source: Source) {
        viewModel.switchSource(source)

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
