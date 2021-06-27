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
import com.flamyoad.honnoki.source.BaseSource
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.source.model.TabType
import com.flamyoad.honnoki.ui.home.adapter.MangaListFragmentAdapter
import com.flamyoad.honnoki.ui.home.dialog.GenrePickerDialog
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy
import com.flamyoad.honnoki.utils.ui.DepthPageTransformer
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.qualifier.named

@ExperimentalPagingApi
class HomeFragment : BaseFragment(), KoinComponent, SourceSwitcherDialog.Listener {
    private val binding by viewLifecycleLazy { FragmentHomeBinding.bind(requireView()) }

    private val viewModel: HomeViewModel by sharedViewModel()

    private val viewPagerAdapter by lazy { MangaListFragmentAdapter(this) }

    private var tabLayoutMediator: TabLayoutMediator? = null

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
        observeUi()
    }

    private fun initUi() {
        with(binding.viewPager) {
            adapter = viewPagerAdapter
            setPageTransformer(DepthPageTransformer())
        }

        binding.fab.setOnClickListener {
            val dialog = SourceSwitcherDialog.newInstance(this@HomeFragment)
            dialog.show(childFragmentManager, SourceSwitcherDialog.TAG)
        }

        binding.btnGenre.setOnClickListener {
            val source = viewModel.getSource() ?: return@setOnClickListener
            val dialog = GenrePickerDialog.newInstance(source)
            dialog.show(childFragmentManager, GenrePickerDialog.TAG)
        }
    }

    private fun observeUi() {
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

        lifecycleScope.launchWhenResumed {
            viewModel.chosenSource.collectLatest { source ->
                tabLayoutMediator?.detach()

                val sourceImpl: BaseSource = getKoin().get(named(source.name))
                viewPagerAdapter.setSource(sourceImpl)
                attachTabLayoutMediator(sourceImpl.getAvailableTabs())
            }
        }
    }

    private fun attachTabLayoutMediator(tabType: List<TabType>) {
        val mediator = TabLayoutMediator(binding.tabLayoutMain, binding.viewPager) { tab, position ->
            tab.text = tabType[position].readableName
        }
        mediator.attach()

        tabLayoutMediator = mediator
    }

    override fun onSourceSwitch(source: Source) {
        viewModel.switchSource(source)

        // Dismiss the dialog
        childFragmentManager.findFragmentByTag(SourceSwitcherDialog.TAG).let {
            if (it is DialogFragment) {
                it.dismiss()
            }
        }
    }

    override val bottomBarTitle: String
        get() = "Home"

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

}
