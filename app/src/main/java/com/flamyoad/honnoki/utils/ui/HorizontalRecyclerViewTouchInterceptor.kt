package com.flamyoad.honnoki.utils.ui

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

fun RecyclerView.willConsumeHorizontalScrolls() {
    addOnItemTouchListener(HorizontalRecyclerViewTouchInterceptor())
}

class HorizontalRecyclerViewTouchInterceptor() : RecyclerView.OnItemTouchListener {

    private var prevX: Float = 0f
    private var prevY: Float = 0f

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                prevX = e.x
                prevY = e.y
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = e.x - prevX
                val deltaY = e.y - prevY
                if ((abs(deltaX) > abs(deltaY))) { // Horizontal
                    rv.parent.requestDisallowInterceptTouchEvent(true)
                } else { // Vertical
                    rv.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
        }

        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }
}