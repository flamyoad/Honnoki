package com.flamyoad.honnoki.ui.reader.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.ui.reader.ReaderViewModel
import com.flamyoad.honnoki.ui.reader.model.ReaderViewMode

class BottomActionMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private val btnChapters: TextView
    private val btnViewMode: TextView
    private val btnOrientation: TextView
    private val btnBookmark: TextView

    var onChapterListClick: (() -> Unit)? = null
    var onViewModeClick: (() -> Unit)? = null
    var onOrientationClick: (() -> Unit)? = null
    var onBookmarkClick: (() -> Unit)? = null

    init {
        inflate(context, R.layout.bottom_action_menu, this)
        btnChapters = findViewById(R.id.btnChapters)
        btnViewMode = findViewById(R.id.btnViewMode)
        btnOrientation = findViewById(R.id.btnOrientation)
        btnBookmark = findViewById(R.id.btnBookmark)

        btnChapters.setOnClickListener { onChapterListClick?.invoke() }
        btnViewMode.setOnClickListener { onViewModeClick?.invoke() }
        btnOrientation.setOnClickListener { onOrientationClick?.invoke() }
        btnBookmark.setOnClickListener { onBookmarkClick?.invoke() }
    }
}