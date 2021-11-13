package com.flamyoad.honnoki.common

import androidx.fragment.app.Fragment
import com.flamyoad.honnoki.common.navigator.Navigator

open class BaseFragment : Fragment() {
    val navigator: Navigator get() = (requireActivity() as BaseActivity).navigator
}