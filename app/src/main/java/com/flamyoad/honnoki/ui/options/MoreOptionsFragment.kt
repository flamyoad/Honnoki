package com.flamyoad.honnoki.ui.options

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.flamyoad.honnoki.BaseFragment
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.FragmentMoreOptionsBinding

class MoreOptionsFragment : BaseFragment() {

    private var _binding: FragmentMoreOptionsBinding? = null
    val binding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getTitle(): String = "More"

    companion object {
        @JvmStatic
        fun newInstance() = MoreOptionsFragment()
    }
}