package com.flamyoad.honnoki.dialog

import com.flamyoad.honnoki.common.BaseViewModel
import com.flamyoad.honnoki.repository.system.BrightnessRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ScreenBrightnessViewModel(private val brightnessRepository: BrightnessRepository) :
    BaseViewModel() {

    // Returns the value (int) to be used in Slider
    val userBrightness: Int =
        runBlocking {
            (brightnessRepository.getUserBrightness().first() * 100).toInt()
        }

    val useSystemBrightness: Flow<Boolean> =
        brightnessRepository.useSystemBrightness()

    fun setUserBrightness(sliderValue: Int) {
        val percentage = (sliderValue.toFloat() / 100)
        if (percentage > 1f) {
            brightnessRepository.setUserBrightness(1f)
        } else {
            brightnessRepository.setUserBrightness(percentage)
        }
    }

    fun setUseSystemBrightness(value: Boolean) =
        brightnessRepository.setUseSystemBrightness(value)
}