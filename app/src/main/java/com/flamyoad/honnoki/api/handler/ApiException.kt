package com.flamyoad.honnoki.api.handler

class ApiException(val code: Code): RuntimeException() {
    enum class Code() {
        // Error 4xx
        BadRequest,
        Unauthorized,
        Forbidden,
        UrlNotFound,

        // Error 5xx
        BadGateway,

        NoInternetConnection,
        JsonDataMismatch,
        UnspecifiedError
    }
}