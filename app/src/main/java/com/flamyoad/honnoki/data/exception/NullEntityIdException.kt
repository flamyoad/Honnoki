package com.flamyoad.honnoki.data.exception

class NullEntityIdException(message: String = "The id of the entity passed in is null") :
    RuntimeException(message) {
}