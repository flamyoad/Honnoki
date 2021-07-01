package com.flamyoad.honnoki.ui.options

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flamyoad.honnoki.BaseFragment
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.FragmentMoreOptionsBinding
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class OptionsFragment : Fragment() {

    private var _binding: FragmentMoreOptionsBinding? = null
    val binding get() = requireNotNull(_binding)

    private val viewModel: OptionsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSource.setOnClickListener {
            val action = OptionsFragmentDirections.actionOptionsFragmentToDefaultSourceFragment()
            findNavController().navigate(action)
        }

        binding.swNightMode.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setNightMode(isChecked)
        }

        lifecycleScope.launchWhenResumed {
            viewModel.nightModeEnabled.collectLatest {
                binding.swNightMode.isChecked = it
                enableNightMode(it)
            }
        }
    }

    private fun enableNightMode(yes: Boolean) {
        if (yes) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = OptionsFragment()
    }
}