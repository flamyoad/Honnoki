package com.flamyoad.honnoki.ui.onboarding

import androidx.lifecycle.ViewModel
import com.flamyoad.honnoki.repository.theme.ThemeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val themeRepository: ThemeRepository,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    fun setOnboardingCompleted() {
        applicationScope.launch(Dispatchers.IO) {
            themeRepository.setCompletedOnboarding(true)
        }
    }
}