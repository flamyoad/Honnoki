package com.flamyoad.honnoki.ui.onboarding.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.flamyoad.honnoki.ui.onboarding.UiModeFragment
import java.lang.IllegalArgumentException

class OnboardingFragmentAdapter(activity: FragmentActivity) :
    FragmentStateAdapter(activity) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UiModeFragment.newInstance()
            1 -> UiModeFragment . newInstance ()
            else -> throw IllegalArgumentException()
        }
    }
}