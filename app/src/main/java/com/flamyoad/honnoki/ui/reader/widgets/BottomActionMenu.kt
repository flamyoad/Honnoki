package com.flamyoad.honnoki.ui.reader.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.flamyoad.honnoki.R

class BottomActionMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    init {
        inflate(context, R.layout.bottom_action_menu, this)
    }
}