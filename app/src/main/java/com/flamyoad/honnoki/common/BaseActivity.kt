package com.flamyoad.honnoki.common

import androidx.appcompat.app.AppCompatActivity
import com.flamyoad.honnoki.common.navigator.Navigator
import org.koin.android.ext.android.inject

open class BaseActivity: AppCompatActivity() {
    val navigator: Navigator by inject()
}