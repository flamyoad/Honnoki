package com.flamyoad.honnoki

import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {
    abstract fun getTitle(): String
}