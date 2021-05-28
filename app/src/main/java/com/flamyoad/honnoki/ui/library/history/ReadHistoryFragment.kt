package com.flamyoad.honnoki.ui.library.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.flamyoad.honnoki.databinding.FragmentReadHistoryBinding
import com.flamyoad.honnoki.utils.ui.ToggleState

class ReadHistoryFragment : Fragment() {

    private var _binding: FragmentReadHistoryBinding? = null
    val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentReadHistoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val toggle = toggleView
            btnOff.setOnClickListener {
                toggle.setView(ToggleState.OFF)
            }
            btnOn.setOnClickListener {
                toggle.setView(ToggleState.ON)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ReadHistoryFragment()
    }
}
