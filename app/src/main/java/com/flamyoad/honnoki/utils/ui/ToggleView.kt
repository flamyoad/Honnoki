package com.flamyoad.honnoki.utils.ui

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.view.View
import androidx.core.view.isVisible
import com.flamyoad.honnoki.R
import java.lang.IllegalArgumentException

class ToggleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
): FrameLayout(context, attrs, defStyle) {

    companion object {
        const val TOGGLE_OFF = 0
        const val TOGGLE_ON = 1
    }

    private var viewOnToggle: View? = null
    private var viewOffToggle: View? = null

    var toggleState: ToggleState = ToggleState.OFF
        private set

    var isChecked: Boolean = false
        get() {
            return when(toggleState) {
                ToggleState.ON -> true
                ToggleState.OFF -> false
            }
        }
        set(value) {
            when (value) {
                true -> setView(ToggleState.ON)
                false -> setView(ToggleState.OFF)
            }
            field = value
        }

    init {
        val inflater = LayoutInflater.from(context)
        val attr = context.obtainStyledAttributes(attrs, R.styleable.ToggleView, 0, 0)

        val viewOnToggleId = attr.getResourceId(R.styleable.ToggleView_viewOn, -1)
        if (viewOnToggleId != -1) {
            val inflatedViewOnToggle = inflater.inflate(viewOnToggleId, this, false)
            viewOnToggle = inflatedViewOnToggle
            addView(inflatedViewOnToggle, inflatedViewOnToggle.layoutParams)
        }

        val viewOffToggleId = attr.getResourceId(R.styleable.ToggleView_viewOff, -1)
        if (viewOffToggleId != -1) {
            val inflatedViewOffToggle = inflater.inflate(viewOffToggleId, this, false)
            viewOffToggle = inflatedViewOffToggle
            addView(inflatedViewOffToggle, inflatedViewOffToggle.layoutParams)
        }

        toggleState = when (attr.getInt(R.styleable.ToggleView_initialState, TOGGLE_OFF)) {
            TOGGLE_OFF -> ToggleState.OFF
            TOGGLE_ON -> ToggleState.ON
            else -> ToggleState.OFF
        }

        viewOnToggle?.isVisible = false
        viewOffToggle?.isVisible = false

        attr.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setView(toggleState)
    }

    fun setView(state: ToggleState) {
        viewOffToggle?.isVisible = false
        viewOnToggle?.isVisible = false

        this.toggleState = state

        when (state) {
            ToggleState.OFF -> requireNotNull(viewOffToggle).isVisible = true
            ToggleState.ON -> viewOnToggle!!.isVisible = true
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        return when (superState) {
            null -> superState
            else -> ToggleSaveState(superState, this.toggleState)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        when (state) {
            is ToggleSaveState -> {
                super.onRestoreInstanceState(state.superState)
                this.toggleState = state.toggleState
            }
            else -> {
                super.onRestoreInstanceState(state)
            }
        }
    }

    internal class ToggleSaveState: BaseSavedState {
        val toggleState: ToggleState

        constructor(superState: Parcelable, toggleState: ToggleState) : super(superState) {
            this.toggleState = toggleState
        }

        constructor(source: Parcel) : super(source) {
            val toggleStateName = source.readString()
            toggleState = ToggleState.valueOf(requireNotNull(toggleStateName))
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(toggleState.toString())
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<ToggleSaveState> = object : Parcelable.Creator<ToggleSaveState> {
                override fun createFromParcel(source: Parcel): ToggleSaveState {
                    return ToggleSaveState(source)
                }

                override fun newArray(size: Int): Array<ToggleSaveState> {
                    return newArray(size)
                }
            }
        }
    }
}

enum class ToggleState {
    OFF,
    ON,
}