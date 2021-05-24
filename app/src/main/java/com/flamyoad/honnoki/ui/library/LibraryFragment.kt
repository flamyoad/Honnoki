package com.flamyoad.honnoki.ui.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.flamyoad.honnoki.BaseFragment

import com.flamyoad.honnoki.R

class LibraryFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun getTitle(): String {
        return "Library"
    }

    companion object {
        @JvmStatic
        fun newInstance() = LibraryFragment()
    }
}
