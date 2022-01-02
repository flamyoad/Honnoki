package com.flamyoad.honnoki.repository.system

import android.view.WindowManager
import com.flamyoad.honnoki.data.preference.ReaderPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class BrightnessRepositoryImpl(
    private val pref: ReaderPreference,
    private val coroutineScope: CoroutineScope,
) : BrightnessRepository {

    override fun getBrightness(): Flow<Float> {
        return pref.useSystemBrightness.combine(pref.screenBrightness) { useSystemBrightness, screenBrightness ->
            return@combine if (useSystemBrightness) {
                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            } else {
                screenBrightness
            }
        }
    }

    override fun getUserBrightness(): Flow<Float> {
        return pref.screenBrightness
    }

    override fun setUserBrightness(value: Float) {
        coroutineScope.launch(Dispatchers.IO) {
            pref.editScreenBrightness(value)
        }
    }

    override fun useSystemBrightness(): Flow<Boolean> {
        return pref.useSystemBrightness
    }

    override fun setUseSystemBrightness(value: Boolean) {
        coroutineScope.launch(Dispatchers.IO) {
            pref.editUseSystemBrightness(value)
        }
    }


}