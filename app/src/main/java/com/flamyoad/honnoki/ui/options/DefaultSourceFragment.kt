package com.flamyoad.honnoki.ui.options

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.FragmentDefaultSourceBinding

class DefaultSourceFragment : Fragment() {

    private var _binding: FragmentDefaultSourceBinding? = null
    val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDefaultSourceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with (binding.toolbarLayout.toolbar) {
            setupWithNavController(findNavController())
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            DefaultSourceFragment()
    }
}