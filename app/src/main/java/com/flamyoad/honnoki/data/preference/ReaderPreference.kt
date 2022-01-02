package com.flamyoad.honnoki.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.flamyoad.honnoki.parser.model.MangadexQualityMode
import com.flamyoad.honnoki.source.model.Source
import com.flamyoad.honnoki.ui.reader.model.PageScrollDirection
import com.flamyoad.honnoki.ui.reader.model.ReaderOrientation
import com.flamyoad.honnoki.ui.reader.model.ReaderViewMode
import com.flamyoad.honnoki.utils.extensions.getConvertedValue
import com.flamyoad.honnoki.utils.extensions.getValue

class ReaderPreference(private val dataStore: DataStore<Preferences>) {
    private val VOLUME_UP_ACTION = stringPreferencesKey("volume_up_action")
    private val VOLUME_DOWN_ACTION = stringPreferencesKey("volume_down_action")
    private val MANGADEX_QUALITY_MODE =
        stringPreferencesKey("mangadex_quality_mode")
    private val EXTRA_SPACE_AT_BOTTOM_INDICATOR =
        booleanPreferencesKey("extra_space_at_bottom_indicator")
    private val SHOW_ADS = booleanPreferencesKey("show_ads")
    private val VIEW_MODE = stringPreferencesKey("view_mode")
    private val ORIENTATION = stringPreferencesKey("orientation")
    private val SCREEN_BRIGHTNESS = floatPreferencesKey("screen_brightness")
    private val USE_SYSTEM_BRIGHTNESS =
        booleanPreferencesKey("use_system_brightness")

    fun shouldShowAds(source: Source): Boolean {
        return false
    }

    val mangadexQualityMode =
        dataStore.getConvertedValue(MANGADEX_QUALITY_MODE) {
            MangadexQualityMode.valueOf(
                it ?: MangadexQualityMode.DATA.toString()
            )
        }

    val extraSpaceAtBottomIndicator =
        dataStore.getValue(EXTRA_SPACE_AT_BOTTOM_INDICATOR, false)

    val viewMode = dataStore.getConvertedValue(VIEW_MODE) {
        ReaderViewMode.valueOf(
            it ?: ReaderViewMode.CONTINUOUS_SCROLLING.toString()
        )
    }

    val orientation = dataStore.getConvertedValue(ORIENTATION) {
        ReaderOrientation.valueOf(
            it ?: ReaderOrientation.FREE.toString()
        )
    }

    val screenBrightness =
        dataStore.getValue(SCREEN_BRIGHTNESS, defaultValueIfNull = 0.5f)

    val useSystemBrightness =
        dataStore.getValue(USE_SYSTEM_BRIGHTNESS, defaultValueIfNull = true)

    suspend fun editVolumeUpAction(scrollDir: PageScrollDirection) =
        dataStore.edit { it[VOLUME_UP_ACTION] = scrollDir.toString() }

    suspend fun editVolumeDownAction(scrollDir: PageScrollDirection) =
        dataStore.edit { it[VOLUME_DOWN_ACTION] = scrollDir.toString() }

    suspend fun editMangadexQualityMode(mode: MangadexQualityMode) =
        dataStore.edit { it[MANGADEX_QUALITY_MODE] = mode.toString() }

    suspend fun editExtraSpaceAtBottomIndicator(isEnabled: Boolean) =
        dataStore.edit { it[EXTRA_SPACE_AT_BOTTOM_INDICATOR] = isEnabled }

    suspend fun editReaderViewMode(viewMode: ReaderViewMode) =
        dataStore.edit { it[VIEW_MODE] = viewMode.toString() }

    suspend fun editReaderOrientation(orientation: ReaderOrientation) =
        dataStore.edit { it[ORIENTATION] = orientation.toString() }

    suspend fun editScreenBrightness(brightness: Float) =
        dataStore.edit { it[SCREEN_BRIGHTNESS] = brightness }

    suspend fun editUseSystemBrightness(value: Boolean) =
        dataStore.edit { it[USE_SYSTEM_BRIGHTNESS] = value }
}