package com.flamyoad.honnoki.ui.reader.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.flamyoad.honnoki.R

class SwipeReaderTopIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    private val parentLayout: FrameLayout
    private val layoutLoading: LinearLayout
    private val layoutError: ConstraintLayout
    private val btnRetry: Button
    private val btnCloseError: ImageButton

    val isVisible get(): Boolean = parentLayout.isVisible

    var onRetry: (() -> Unit)? = null

    init {
        inflate(context, R.layout.swipe_reader_top_indicator, this)
        parentLayout = findViewById(R.id.parentLayout)
        layoutLoading = findViewById(R.id.layoutLoading)
        layoutError = findViewById(R.id.layoutError)
        btnRetry = findViewById(R.id.btnRetry)
        btnCloseError = findViewById(R.id.btnCloseError)

        btnRetry.setOnClickListener { onRetry?.invoke() }
        btnCloseError.setOnClickListener { hide() }
    }

    fun hide() {
        parentLayout.isVisible = false
        onRetry = null
    }

    fun showLoading() {
        parentLayout.isVisible = true
        layoutLoading.isVisible = true
        layoutError.isVisible = false
    }

    fun showError(onRetryCallback: () -> Unit) {
        parentLayout.isVisible = true
        layoutError.isVisible = true
        layoutLoading.isVisible = false
        onRetry = onRetryCallback
    }
}