package com.flamyoad.honnoki

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment

abstract class BaseFragment : NavHostFragment() {
    abstract val bottomBarTitle: String

    open val ignoreDefaultBackPressAction = false

    open fun onBackPressAction() {

    }
}