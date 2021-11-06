package com.flamyoad.honnoki.utils.extensions

import android.view.View
import com.kennyc.view.MultiStateView

fun MultiStateView.findViewFromError(viewId: Int): View? {
    return getView(MultiStateView.ViewState.ERROR)?.findViewById(viewId)
}

fun MultiStateView.findViewFromLoading(viewId: Int): View? {
    return getView(MultiStateView.ViewState.LOADING)?.findViewById(viewId)
}

fun MultiStateView.findViewFromEmpty(viewId: Int): View? {
    return getView(MultiStateView.ViewState.EMPTY)?.findViewById(viewId)
}