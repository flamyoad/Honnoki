package com.flamyoad.honnoki.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flamyoad.honnoki.data.UiMode
import com.flamyoad.honnoki.repository.theme.ThemeRepository
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.ui.onboarding.model.SelectedUiMode
import com.flamyoad.honnoki.ui.options.model.SourceOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val themeRepository: ThemeRepository,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    private val selectedUiMode: MutableStateFlow<UiMode> =
        MutableStateFlow(UiMode.SYSTEM_DEFAULT)

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
        selectedUiMode.value = value.uiMode
    }

    fun setOnboardingCompleted() {
        applicationScope.launch(Dispatchers.IO) {
            themeRepository.setCompletedOnboarding(true)
        }
    }
}