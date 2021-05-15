package com.flamyoad.honnoki.parser

import org.jsoup.nodes.Element
import java.lang.IllegalArgumentException

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

fun Element?.parentNonNull(): Element {
    if (this == null) return Element(" ")
    return this.parent()
}

fun Element?.ancestorNonNull(level: Int): Element {
    if (level < 1)
        throw IllegalArgumentException("Ancestor level of less than 1 is not accepted")

    if (this == null) return Element(" ")

    throw NotImplementedError()
}
