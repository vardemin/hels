package com.vardemin.hels.network

import com.vardemin.hels.utils.utf8Size

interface HNetworkLogger {
    /**
     * Log started request without response
     * @param method HTTP request method
     * @param url of request
     * @param headers in request
     * @param bodySize request body size
     * @param bodyString if request body is string
     * @param time request was sent
     * @return request id
     */
    fun logRequest(
        method: String,
        url: String,
        headers: Map<String, List<String>>,
        bodySize: Long,
        bodyString: String?,
        time: Long,
    ): String

    /**
     * Log response by request id
     * @param requestId previously logged request id
     * @param code response HTTP code
     * @param headers of the response
     * @param bodySize response body size in bytes
     * @param bodyString if response body is string
     * @param time response intercepted
     */
    fun logResponse(
        requestId: String,
        code: Int,
        headers: Map<String, List<String>>,
        bodySize: Long,
        bodyString: String?,
        time: Long
    )

    /**
     * Log finished request with response
     * @param method HTTP request method
     * @param url of request
     * @param headers in request
     * @param bodySize of request in bytes
     * @param bodyString if str body in request
     * @param time request was sent
     * @param code response HTTP code
     * @param responseHeaders of the response
     * @param responseBodySize in bytes
     * @param responseBody if response body is string
     * @param responseTime response intercepted
     * @return request id
     */
    fun logFullRequest(
        method: String,
        url: String,
        headers: Map<String, List<String>>,
        bodySize: Long,
        bodyString: String?,
        time: Long,
        code: Int,
        responseHeaders: Map<String, List<String>>,
        responseBodySize: Long,
        responseBody: String?,
        responseTime: Long
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
        time: Long,
        code: Int = UNKNOWN_ERROR_CODE
    ) {
        logResponse(requestId, code, mapOf(), errorMessage.utf8Size(), errorMessage, time)
    }

    companion object {
        const val UNKNOWN_ERROR_CODE = 999
    }
}