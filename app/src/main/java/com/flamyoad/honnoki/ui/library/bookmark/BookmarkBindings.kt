package com.flamyoad.honnoki.ui.library.bookmark

import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.utils.ViewUtils.hideKeyboard
import com.flamyoad.honnoki.utils.ViewUtils.showKeyboard

object BookmarkBindings {
    @JvmStatic
    @BindingAdapter("toggleSearchLogo")
    fun ImageButton.toggleSearchLogo(isSearching: Boolean?) {
        if (isSearching == null) return

        val drawableId = if (isSearching) {
            R.drawable.ic_baseline_close_24_white
        } else {
            R.drawable.ic_search_white_24dp
        }
        Glide.with(this)
            .load(ContextCompat.getDrawable(this.context, drawableId))
            .into(this)
    }

    @JvmStatic
    @BindingAdapter("query")
    fun setQuery(searchView: SearchView, queryText: String?) {
        searchView.setQuery(queryText, false)
    }
}