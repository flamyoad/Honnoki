package com.flamyoad.honnoki.ui.reader.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Px
import androidx.core.view.isVisible
import com.afollestad.materialdialogs.utils.MDUtil.updatePadding
import com.flamyoad.honnoki.R
import com.google.android.material.card.MaterialCardView

class BottomInfoWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private val parentCardView: MaterialCardView
    private val contentLayout: LinearLayout
    private val txtCurrentPage: TextView
    private val txtCurrentChapter: TextView

    init {
        inflate(context, R.layout.bottom_info_widget, this)
        parentCardView = findViewById(R.id.parentCardView)
        contentLayout = findViewById(R.id.contentLayout)
        txtCurrentPage = findViewById(R.id.txtCurrentPageMini)
        txtCurrentChapter = findViewById(R.id.txtCurrentChapterMini)

        val attributes =
            context.obtainStyledAttributes(attrs, R.styleable.BottomInfoWidget)
        txtCurrentPage.text =
            attributes.getString(R.styleable.BottomInfoWidget_currentPage)
        txtCurrentChapter.text =
            attributes.getString(R.styleable.BottomInfoWidget_currentChapter)
        attributes.recycle()
    }

    var currentPage: String
        get() = txtCurrentPage.text.toString()
        set(value) {
            txtCurrentPage.text = value
        }

    var currentChapter: String
        get() = txtCurrentChapter.text.toString()
        set(value) {
            print(value)
            txtCurrentChapter.text = value
            parentCardView.apply {
                invalidate()
                requestLayout()
            }
            txtCurrentChapter.apply {
                invalidate()
                requestLayout()
            }
        }

    var isVisible: Boolean
        get() = parentCardView.isVisible
        set(value) {
            parentCardView.isVisible = value
        }

    fun updatePadding(
        @Px left: Int = paddingLeft,
        @Px top: Int = paddingTop,
        @Px right: Int = paddingRight,
        @Px bottom: Int = paddingBottom
    ) {
        contentLayout.updatePadding(left, top, right, bottom)
    }
}