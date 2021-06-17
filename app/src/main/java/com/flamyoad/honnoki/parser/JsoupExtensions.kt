package com.flamyoad.honnoki.parser

import org.jsoup.nodes.Element
import java.lang.IllegalArgumentException

// Jsoup does not allow element with empty string.
const val EMPTY_TAG = "<p/>"

fun Element?.textNonNull(): String {
    if (this == null) return ""
    return this.text()
}

fun Element?.attrNonNull(attributeKey: String): String {
    if (this == null) return ""
    return this.attr(attributeKey)
}

fun Element?.ownTextNonNull(): String {
    if (this == null) return ""
    return this.ownText()
}

fun Element?.htmlNonNull(): String {
    if (this == null) return ""
    return this.html()
}

fun Element?.parentNonNull(): Element {
    if (this == null) return Element(EMPTY_TAG)
    if (this.hasParent()) {
        return this.parent()
    } else {
        return Element(EMPTY_TAG)
    }
}

/**
 * Helper function for calling [parentNonNull] recursively.
 *
 * For example, ancestorNonNull(2) would mean calling [parentNonNull] on the same object 2 times
 */
fun Element?.ancestorNonNull(level: Int): Element {
    if (level < 1)
        throw IllegalArgumentException("Ancestor level of less than 1 is not accepted")

    if (this == null) return Element(EMPTY_TAG)

    var element: Element = this
    for (i in 0 until level) {
        element = element.parentNonNull()

        if (element == Element(EMPTY_TAG)) {
            return element
        }
    }
    return element
}

