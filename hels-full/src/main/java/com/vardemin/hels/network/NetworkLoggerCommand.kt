package com.vardemin.hels.network

import com.vardemin.hels.command.InternalCommand
import kotlinx.datetime.LocalDateTime

internal sealed interface NetworkLoggerCommand : InternalCommand {
    data class LogRequest(
        val id: String,
        val method: String,
        val url: String,
        val headers: Map<String, List<String>>,
        val bodySize: Long,
        val bodyString: String?,
        val time: LocalDateTime
    ) : NetworkLoggerCommand

    data class LogResponse(
        val requestId: String,
        val code: Int,
        val headers: Map<String, List<String>>,
        val bodySize: Long,
        val bodyString: String?,
        val time: LocalDateTime
    ) : NetworkLoggerCommand

    data class LogFullRequest(
        val id: String,
        val method: String,
        val url: String,
        val headers: Map<String, List<String>>,
        val bodySize: Long,
        val bodyString: String?,
        val time: LocalDateTime,
        val code: Int,
        val responseHeaders: Map<String, List<String>>,
        val responseBodySize: Long,
        val responseBody: String?,
        val responseTime: LocalDateTime
    ) : NetworkLoggerCommand
}