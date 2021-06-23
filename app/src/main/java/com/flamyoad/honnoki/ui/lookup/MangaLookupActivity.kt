package com.flamyoad.honnoki.ui.lookup

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.data.Source
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MangaLookupActivity : AppCompatActivity() {

    private val sourceName: String by lazy { intent.getStringExtra(SOURCE_NAME) ?: "" }

    private val viewModel: MangaLookupViewModel by viewModel { parametersOf(sourceName) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manga_lookup)
    }

    companion object {
        const val SOURCE_NAME = "source_name"

        fun startActivity(context: Context, source: Source) {
            val intent = Intent().apply {
                putExtra(SOURCE_NAME, source.name)
            }
            context.startActivity(intent)
        }
    }
}