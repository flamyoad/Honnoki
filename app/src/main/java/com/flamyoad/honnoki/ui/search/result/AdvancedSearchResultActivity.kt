package com.flamyoad.honnoki.ui.search.result

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.ui.search.SimpleSearchViewModel

class AdvancedSearchResultActivity : AppCompatActivity() {

    private val viewModel: AdvancedSearchResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advanced_search_result)
    }

    companion object {
        const val SEARCH_KEYWORD = "search_keyword"
    }
}
