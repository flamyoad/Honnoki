package com.flamyoad.honnoki.ui.overview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.flamyoad.honnoki.R
import com.flamyoad.honnoki.ui.theme.ActionIconTintGrey
import com.flamyoad.honnoki.ui.theme.HonnokiTheme
import com.google.android.material.composethemeadapter.MdcTheme

class DownloadPickerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MdcTheme {
                Column {
                    AppBar(onBackButtonClick = { finish() })
                    Row {
                        Column(modifier = Modifier.weight(1.0f, true)) {
                            TotalChaptersText(totalChapters = 30)
                        }
                        Column {
                            ChapterActionMenu()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppBar(onBackButtonClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                "Download Manga",
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        },
        navigationIcon = {
            IconButton(onClick = { onBackButtonClick() }) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
        }
    )
}

@Composable
fun TotalChaptersText(totalChapters: Int) {
    Text(
        text = "${totalChapters.toString()} chapters",
        color = colorResource(R.color.colorPrimaryLight),
    )
}

@Composable
fun ChapterActionMenu() {
    Row {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Filled.List, "", tint = ActionIconTintGrey)
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Filled.ThumbUp, "", tint = ActionIconTintGrey)
        }
    }
}

@Composable
fun ChapterList() {

}