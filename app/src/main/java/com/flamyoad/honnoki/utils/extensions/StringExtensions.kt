package com.flamyoad.honnoki.utils.extensions

import java.util.*

/**
 * Quick substitution for the deprecated capitalize() method
 */
fun String.capitalizeWithLocale(): String {
    return replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase(Locale.getDefault())
        else it.toString()
    }
}