package com.vardemin.hels

import com.vardemin.hels.log.EmptyLogger
import com.vardemin.hels.log.HLogger
import com.vardemin.hels.network.EmptyNetworkLogger
import com.vardemin.hels.network.HNetworkLogger
import com.vardemin.hels.server.EmptyServer
import com.vardemin.hels.server.HServer
import kotlinx.datetime.LocalDateTime

object Hels : HServer, HLogger, HNetworkLogger {
    private val logger: HLogger get() = loggerInstance ?: EmptyLogger
    private val networkLogger: HNetworkLogger get() = networkLoggerInstance ?: EmptyNetworkLogger
    private val server: HServer get() = serverInstance ?: EmptyServer

    override fun start() {
        server.start()
    }

    override fun stop() {
        server.stop()
    }

    override fun d(tag: String, message: String, properties: Map<String, String>) {
        logger.d(tag, message, properties)
    }

    override fun v(tag: String, message: String, properties: Map<String, String>) {
        logger.v(tag, message, properties)
    }

    override fun i(tag: String, message: String, properties: Map<String, String>) {
        logger.i(tag, message, properties)
    }

    override fun logRequest(
        method: String,
        url: String,
        headers: Map<String, List<String>>,
        body: String?,
        time: LocalDateTime
    ): String {
        return networkLogger.logRequest(method, url, headers, body, time)
    }

    override fun logResponse(
        requestId: String,
        code: Int,
        headers: Map<String, List<String>>,
        body: String?,
        time: LocalDateTime
    ) {
        networkLogger.logResponse(requestId, code, headers, body, time)
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
    ): String {
        return networkLogger.logFullRequest(
            method,
            url,
            headers,
            body,
            time,
            code,
            responseHeaders,
            responseBody,
            responseTime
        )
    }
}