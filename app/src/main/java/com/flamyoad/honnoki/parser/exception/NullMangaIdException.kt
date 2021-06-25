package com.flamyoad.honnoki.parser.exception

import java.io.IOException

/**
 * Exception thrown when the id given in the JSON is null
 */
class NullMangaIdException: IOException() {

    override val message: String
        get() = "Manga ID in the JSON is null!"
}