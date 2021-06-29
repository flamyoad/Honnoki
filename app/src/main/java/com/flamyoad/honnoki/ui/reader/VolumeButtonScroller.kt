package com.flamyoad.honnoki.ui.reader

import android.view.KeyEvent
import com.flamyoad.honnoki.data.preference.ReaderPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onStart
import java.util.concurrent.TimeUnit

class VolumeButtonScroller(
    private val listener: Listener,
    private val readerPrefs: ReaderPreference,
) {

    fun sendKeyEvent(event: KeyEvent?): Boolean {
        val keyCode = event?.keyCode
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    listener.onNextPage()
                }
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    listener.onPrevPage()
                }
            }
            return true
        }
        return false
    }

    interface Listener {
        fun onNextPage()
        fun onPrevPage()
        fun scrollByFixedDistance(distance: Int)
    }
}