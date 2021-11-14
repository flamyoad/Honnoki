package com.flamyoad.honnoki.ui.onboarding

import androidx.lifecycle.viewModelScope
import com.flamyoad.honnoki.common.BaseViewModel
import com.flamyoad.honnoki.data.UiMode
import com.flamyoad.honnoki.repository.theme.ThemeRepository
import com.flamyoad.honnoki.ui.onboarding.model.SelectedUiMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val themeRepository: ThemeRepository,
    private val applicationScope: CoroutineScope
) : BaseViewModel() {

    private val selectedUiMode: SharedFlow<UiMode?> =
        themeRepository.getUiMode().toStateFlow(null)

    fun selectedUiMode() = selectedUiMode
        .distinctUntilChanged()
        .toSharedFlow()

    val uiModeList: StateFlow<List<SelectedUiMode>> = selectedUiMode
        .map { uiMode ->
            UiMode.values()
                .filter { it != UiMode.LIGHT }
                .map {
                    SelectedUiMode(
                        uiMode = it,
                        isSelected = it == uiMode
                    )
                }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun selectUiMode(value: SelectedUiMode) {
        applicationScope.launch {
            themeRepository.setUiMode(value.uiMode)
        }
    }

    fun setOnboardingCompleted() {
        applicationScope.launch(Dispatchers.IO) {
            themeRepository.setCompletedOnboarding(true)
        }
    }
}