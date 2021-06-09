package com.flamyoad.honnoki

import android.content.Context
import androidx.fragment.app.Fragment
import java.lang.ClassCastException

abstract class BaseFragment : Fragment() {
    open fun getTitle(): String = ""
}