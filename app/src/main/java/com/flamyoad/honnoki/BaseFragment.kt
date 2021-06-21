package com.flamyoad.honnoki

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    open fun getTitle(): String = ""
}