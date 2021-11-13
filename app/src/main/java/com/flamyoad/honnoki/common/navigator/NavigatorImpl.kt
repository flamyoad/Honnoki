package com.flamyoad.honnoki.common.navigator

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import com.flamyoad.honnoki.MainActivity
import com.flamyoad.honnoki.repository.theme.ThemeRepository
import com.flamyoad.honnoki.ui.onboarding.OnboardingActivity

@ExperimentalPagingApi
class NavigatorImpl(private val themeRepository: ThemeRepository): Navigator {
    override fun onMainEntry(context: Context) {
        when (themeRepository.getCompletedOnboarding()) {
            true -> showHome(context)
            false -> showOnboarding(context)
        }
    }

    override fun showOnboarding(context: Context) {
        OnboardingActivity.startActivity(context)
    }

    override fun showHome(context: Context) {
        MainActivity.startActivity(context)
    }
}