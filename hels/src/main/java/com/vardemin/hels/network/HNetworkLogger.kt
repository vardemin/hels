package com.vardemin.hels.network

import kotlinx.datetime.LocalDateTime

interface HNetworkLogger {
    /**
     * Log started request without response
     * @param method HTTP request method
     * @param url of request
     * @param headers in request
     * @param body str in request
     * @param time request was sent
     * @return request id
     */
    fun logRequest(
        method: String,
        url: String,
        headers: Map<String, List<String>>,
        body: String?,
        time: LocalDateTime,
    ): String

    /**
     * Log response by request id
     * @param requestId previously logged request id
     * @param code response HTTP code
     * @param headers of the response
     * @param body str in response
     * @param time response intercepted
     */
    fun logResponse(
        requestId: String,
        code: Int,
        headers: Map<String, List<String>>,
        body: String?,
        time: LocalDateTime
    )

    /**
     * Log finished request with response
     * @param method HTTP request method
     * @param url of request
     * @param headers in request
     * @param body str in request
     * @param time request was sent
     * @param code response HTTP code
     * @param responseHeaders of the response
     * @param responseBody str in response
     * @param responseTime response intercepted
     * @return request id
     */
    fun logFullRequest(
        method: String,
        url: String,
        headers: Map<String, List<String>>,
        body: String?,
        time: LocalDateTime,
        code: Int,
        responseHeaders: Map<String, List<String>>,
        responseBody: String?,
        responseTime: LocalDateTime
    ): String

    /**
     * Log request error message
     * @param requestId previously logged request id
     * @param errorMessage to log
     * @param time error occurred
     * @param code of error or 999 if unknown
     */
    fun logRequestError(
        requestId: String,
        errorMessage: String,
        time: LocalDateTime,
        code: Int = UNKNOWN_ERROR_CODE
    ) {
        logResponse(requestId, code, mapOf(), errorMessage, time)
    }

    companion object {
        const val UNKNOWN_ERROR_CODE = 999
    }
}