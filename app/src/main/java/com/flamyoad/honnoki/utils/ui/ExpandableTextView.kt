package com.flamyoad.honnoki.utils.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
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
            if (lineCount < LINE_LIMIT) return@post

            setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_arrow_drop_down_black_24dp)
            maxLines = LINE_LIMIT
        }
    }

    override fun onClick(v: View?) { /* Toggle between expanded collapsed states */
        post {
            if (maxLines == Int.MAX_VALUE) {
                maxLines = LINE_LIMIT
                setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_arrow_drop_down_black_24dp)
            } else {
                maxLines = Int.MAX_VALUE
                setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_arrow_drop_up_black_24dp)
            }
        }
    }

    companion object {
        private const val LINE_LIMIT = 5
        private const val LINE_HEIGHT_BEFORE_ROTATION = "LINE_HEIGHT_BEFORE_ROTATION"
    }
}