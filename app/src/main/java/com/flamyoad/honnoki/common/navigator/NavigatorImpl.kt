package com.flamyoad.honnoki.common.navigator

import android.app.Application
import android.content.Context
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.MainActivity
import com.flamyoad.honnoki.MyApplication
import com.flamyoad.honnoki.repository.theme.ThemeRepository
import com.flamyoad.honnoki.ui.onboarding.OnboardingActivity

@ExperimentalPagingApi
class NavigatorImpl(
    private val themeRepository: ThemeRepository,
    private val application: Application
) : Navigator {

    private var hasCompletedEntryPoint: Boolean
        get() {
            return (application as MyApplication).hasCompletedEntryPoint
        }
        set(value) {
            (application as MyApplication).hasCompletedEntryPoint = value
        }

    override fun onMainEntry(context: Context) {
        if (hasCompletedEntryPoint) return
        when (themeRepository.getCompletedOnboarding()) {
            true -> showHome(context)
            false -> showOnboarding(context)
        }
    }

    override fun showOnboarding(context: Context) {
        OnboardingActivity.startActivity(context)
        hasCompletedEntryPoint = true
    }

    override fun showHome(context: Context) {
        MainActivity.startActivity(context)
        hasCompletedEntryPoint = true
    }
}