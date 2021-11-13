package com.flamyoad.honnoki.common.navigator

import android.content.Context

interface Navigator {
    fun onMainEntry(context: Context)
    fun showOnboarding(context: Context)
    fun showHome(context: Context)
}