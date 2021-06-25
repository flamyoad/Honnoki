package com.flamyoad.honnoki.data.exception

class NullEntityIdException() : RuntimeException() {

    override val message: String
        get() = "The id of the entity passed in is null"
}