package com.vardemin.hels

import com.vardemin.hels.log.HLogger
import com.vardemin.hels.log.LogsEmitter
import com.vardemin.hels.network.NetworkLogsEmitter
import com.vardemin.hels.network.HNetworkLogger
import com.vardemin.hels.server.EmptyServer
import com.vardemin.hels.server.HServer

object Hels : HelsFacade {
    private val logger: HLogger = LogsEmitter
    private val networkLogger: HNetworkLogger = NetworkLogsEmitter
    private val server: HServer get() = serverInstance ?: EmptyServer

    override fun start() {
        server.start()
    }

    override fun stop() {
        server.stop()
    }

    override fun logRequest(
        method: String,
        url: String,
        headers: Map<String, List<String>>,
        bodySize: Long,
        bodyString: String?,
        time: Long
    ): String {
        return networkLogger.logRequest(method, url, headers, bodySize, bodyString, time)
    }

    override fun logResponse(
        requestId: String,
        code: Int,
        headers: Map<String, List<String>>,
        bodySize: Long,
        bodyString: String?,
        time: Long
    ) {
        networkLogger.logResponse(requestId, code, headers, bodySize, bodyString, time)
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
        return networkLogger.logFullRequest(
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
    }

    override fun d(tag: String, message: String) {
        logger.d(tag, message)
    }

    override fun v(tag: String, message: String) {
        logger.v(tag, message)
    }

    override fun i(tag: String, message: String) {
        logger.i(tag, message)
    }

    override fun e(tag: String, message: String) {
        logger.e(tag, message)
    }

    override fun e(tag: String, throwable: Throwable?) {
        logger.e(tag, throwable)
    }

    override fun event(title: String, message: String, properties: Map<String, String>) {
        logger.event(title, message, properties)
    }

    override fun setAttributes(vararg attrs: Pair<String, String>) {
        logger.setAttributes(*attrs)
    }
}