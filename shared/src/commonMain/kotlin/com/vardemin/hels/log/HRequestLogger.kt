package com.vardemin.hels.log

interface HRequestLogger {
    fun generateId(): String

    fun logRequest(
        id: String,
        method: String,
        url: String,
        headers: Map<String, String>,
        payload: String?,
    )

    fun logResponse(
        id: String,
        code: Int,
        headers: Map<String, String>,
        payload: String?,
    )

    fun logError(
        id: String,
        message: String,
    )
}