package com.flamyoad.honnoki.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment

import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.databinding.FragmentHomeBinding
import com.flamyoad.honnoki.utils.extensions.viewLifecycleLazy

class HomeFragment : Fragment() {

    private val binding by viewLifecycleLazy { FragmentHomeBinding.bind(requireView()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_home_menu, menu)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}
