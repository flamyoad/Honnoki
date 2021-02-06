package com.flamyoad.honnoki.dialog

enum class CallerType {
    ACTIVITY,
    FRAGMENT;

    companion object {
        const val ARGS_KEY = "CallerType"
    }
}