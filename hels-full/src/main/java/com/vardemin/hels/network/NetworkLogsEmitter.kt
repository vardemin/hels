package com.vardemin.hels.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.UUID

internal object NetworkLogsEmitter : HNetworkLogger {
    private const val REPLAY_CACHE_SIZE = 256
    private const val BUFFER_SIZE = 256

    private val mutableCommandFlow = MutableSharedFlow<NetworkLoggerCommand>(
        replay = REPLAY_CACHE_SIZE,
        extraBufferCapacity = BUFFER_SIZE
    )

    val commandFlow = mutableCommandFlow.asSharedFlow()

    override fun logRequest(
        method: String,
        url: String,
        headers: Map<String, List<String>>,
        bodySize: Long,
        bodyString: String?,
        time: Long
    ): String {
        val id = UUID.randomUUID().toString()
        mutableCommandFlow.tryEmit(
            NetworkLoggerCommand.LogRequest(
                id, method, url, headers, bodySize, bodyString, time
            )
        )
        return id
    }

    override fun logResponse(
        requestId: String,
        code: Int,
        headers: Map<String, List<String>>,
        bodySize: Long,
        bodyString: String?,
        time: Long
    ) {
        mutableCommandFlow.tryEmit(
            NetworkLoggerCommand.LogResponse(
                requestId, code, headers, bodySize, bodyString, time
            )
        )
    }

    override fun logFullRequest(
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
    ): String {
        val id = UUID.randomUUID().toString()
        mutableCommandFlow.tryEmit(
            NetworkLoggerCommand.LogFullRequest(
                id,
                method,
                url,
                headers,
                bodySize,
                bodyString,
                time,
                code,
                responseHeaders,
                responseBodySize,
                responseBody,
                responseTime
            )
        )
        return id
    }

}