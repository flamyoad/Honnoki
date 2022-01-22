package com.flamyoad.honnoki.utils.ui

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatTextView
import com.flamyoad.honnoki.R

class ExpandableTextView : AppCompatTextView, View.OnClickListener {

    constructor(context: Context) : super(context) { setOnClickListener(this) }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) { setOnClickListener(this) }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { setOnClickListener(this) }

//    override fun onSaveInstanceState(): Parcelable? {
//        super.onSaveInstanceState()
//        return bundleOf(LINE_HEIGHT_BEFORE_ROTATION to maxLines)
//    }
//
//    override fun onRestoreInstanceState(state: Parcelable?) {
//        super.onRestoreInstanceState(state)
//        if (state is Bundle) {
//            val lineHeightBeforeRotation = state.getInt(LINE_HEIGHT_BEFORE_ROTATION)
//            maxLines = lineHeightBeforeRotation
//        }
//    }

    /* This onTextChanged() only runs once during initialization
    *   For subsequent expanding & collapsing, only onClick is called() */
    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        post {
            // No need to do anything if text does not exceed line limit
            if (lineCount < LINE_LIMIT) {
                return@post
            }

            val uiMode = context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
            val downArrow = when (uiMode) {
                Configuration.UI_MODE_NIGHT_YES -> R.drawable.ic_baseline_arrow_drop_down_white_24
                Configuration.UI_MODE_NIGHT_NO -> R.drawable.ic_arrow_drop_down_black_24dp
                else -> R.drawable.ic_arrow_drop_down_black_24dp
            }
            
            setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, downArrow)
            maxLines = LINE_LIMIT
        }
    }

    override fun onClick(v: View?) { /* Toggle between expanded collapsed states */
        post {
            val uiMode = context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)

            if (maxLines == Int.MAX_VALUE) {
                maxLines = LINE_LIMIT
                val downArrow = when (uiMode) {
                    Configuration.UI_MODE_NIGHT_YES -> R.drawable.ic_baseline_arrow_drop_down_white_24
                    Configuration.UI_MODE_NIGHT_NO -> R.drawable.ic_arrow_drop_down_black_24dp
                    else -> R.drawable.ic_arrow_drop_down_black_24dp
                }
                setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, downArrow)
            } else {
                maxLines = Int.MAX_VALUE
                val upArrow = when (uiMode) {
                    Configuration.UI_MODE_NIGHT_YES -> R.drawable.ic_baseline_arrow_drop_up_white_24
                    Configuration.UI_MODE_NIGHT_NO -> R.drawable.ic_arrow_drop_up_black_24dp
                    else -> R.drawable.ic_arrow_drop_up_black_24dp
                }
                setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, upArrow)
            }
        }
    }

    companion object {
        private const val LINE_LIMIT = 5
        private const val LINE_HEIGHT_BEFORE_ROTATION = "LINE_HEIGHT_BEFORE_ROTATION"
    }
}