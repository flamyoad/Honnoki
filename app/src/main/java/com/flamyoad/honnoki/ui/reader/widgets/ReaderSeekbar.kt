package com.flamyoad.honnoki.ui.reader.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.flamyoad.honnoki.R

class ReaderSeekbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private val btnToFirstPage: ImageButton
    private val btnToLastPage: ImageButton
    private val txtSeekbarCurrentPage: TextView
    private val seekBar: SeekBar

    var onUserProgressChanged: ((Int) -> Unit)? = null

    var onStartTrackingTouch: (() -> Unit)? = null

    var onStopTrackingTouch: (() -> Unit)? = null

    var onLeftButtonClick: (() -> Unit)? = null

    var onRightButtonClick: (() -> Unit)? = null

    var text: String
        get() = txtSeekbarCurrentPage.text.toString()
        set(value) {
            txtSeekbarCurrentPage.text = value
        }

    var current: Int
        get() = seekBar.progress
        set(value) {
            seekBar.progress = value
        }

    var max: Int
        get() = seekBar.max
        set(value) {
            seekBar.max = value
        }

    init {
        inflate(context, R.layout.reader_seekbar, this)
        btnToFirstPage = findViewById(R.id.btnToFirstPage)
        btnToLastPage = findViewById(R.id.btnToLastPage)
        txtSeekbarCurrentPage = findViewById(R.id.txtSeekbarCurrentPage)
        seekBar = findViewById(R.id.seekBar)

        btnToFirstPage.setOnClickListener { onLeftButtonClick?.invoke() }
        btnToLastPage.setOnClickListener { onRightButtonClick?.invoke() }

        seekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    onUserProgressChanged?.invoke(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                onStartTrackingTouch?.invoke()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                onStopTrackingTouch?.invoke()
            }
        })
    }
}