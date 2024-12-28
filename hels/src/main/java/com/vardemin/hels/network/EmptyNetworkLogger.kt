package com.vardemin.hels.network

import kotlinx.datetime.LocalDateTime

object EmptyNetworkLogger: HNetworkLogger {
    override fun logRequest(
        method: String,
        url: String,
        headers: Map<String, List<String>>,
        body: String?,
        time: LocalDateTime
    ): String {
        // No op
        return ""
    }

    override fun logResponse(
        requestId: String,
        code: Int,
        headers: Map<String, List<String>>,
        body: String?,
        time: LocalDateTime
    ) {
        // No op
    }

    override fun logFullRequest(
        method: String,
        url: String,
        headers: Map<String, List<String>>,
        body: String?,
        time: LocalDateTime,
        code: Int,
        responseHeaders: Map<String, List<String>>,
        responseBody: String?,
        responseTime: LocalDateTime
    ) : String {
        // No op
        return ""
    }

}