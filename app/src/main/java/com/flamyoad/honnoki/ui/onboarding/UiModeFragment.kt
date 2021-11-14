package com.flamyoad.honnoki.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.flamyoad.honnoki.databinding.FragmentUiModeBinding
import com.flamyoad.honnoki.ui.onboarding.adapter.UiModeAdapter
import com.flamyoad.honnoki.ui.onboarding.model.SelectedUiMode
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UiModeFragment : Fragment(), UiModeAdapter.Listener {

    private val viewModel: OnboardingViewModel by sharedViewModel()

    private var _binding: FragmentUiModeBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val uiModeAdapter by lazy { UiModeAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUiModeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.listUiModes.apply {
            adapter = uiModeAdapter
            layoutManager = gridLayoutManager
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false
            setHasFixedSize(true)
        }
        lifecycleScope.launchWhenResumed {
            viewModel.uiModeList.collectLatest { uiModeAdapter.submitList(it) }
        }
    }

    override fun onUiModeClick(item: SelectedUiMode) {
        viewModel.selectUiMode(item)
    }

    companion object {
        @JvmStatic
        fun newInstance() = UiModeFragment()
    }
}