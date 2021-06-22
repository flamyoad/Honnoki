package com.flamyoad.honnoki

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    abstract val bottomBarTitle: String

    open val ignoreBackPressDefaultAction = false

    open fun onBackPressAction() {

    }
}