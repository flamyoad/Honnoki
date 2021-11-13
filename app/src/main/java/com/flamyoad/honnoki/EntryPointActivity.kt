package com.flamyoad.honnoki

import android.os.Bundle
import com.flamyoad.honnoki.common.BaseActivity

class EntryPointActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigator.onMainEntry(this)
    }
}