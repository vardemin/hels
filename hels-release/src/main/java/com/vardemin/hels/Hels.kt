package com.vardemin.hels

import kotlinx.datetime.LocalDateTime

object Hels : HelsFacade {
    override fun start() {
        // No op
    }

    override fun stop() {
        // No op
    }

    override fun d(tag: String, message: String) {
        // No op
    }

    override fun v(tag: String, message: String) {
        // No op
    }

    override fun i(tag: String, message: String) {
        // No op
    }

    override fun e(tag: String, message: String) {
        // No op
    }

    override fun e(tag: String, throwable: Throwable?) {
        // No op
    }

    override fun event(title: String, message: String, properties: Map<String, String>) {
        // No op
    }

    override fun setAttributes(vararg attrs: Pair<String, String>) {
        // No op
    }

    override fun logRequest(
        method: String,
        url: String,
        headers: Map<String, List<String>>,
        bodySize: Long,
        bodyString: String?,
        time: LocalDateTime
    ): String {
        // No op
        return ""
    }

    override fun logResponse(
        requestId: String,
        code: Int,
        headers: Map<String, List<String>>,
        bodySize: Long,
        bodyString: String?,
        time: LocalDateTime
    ) {
        // No op
    }

    override fun logFullRequest(
        method: String,
        url: String,
        headers: Map<String, List<String>>,
        bodySize: Long,
        bodyString: String?,
        time: LocalDateTime,
        code: Int,
        responseHeaders: Map<String, List<String>>,
        responseBodySize: Long,
        responseBody: String?,
        responseTime: LocalDateTime
    ): String {
        // No op
        return ""
    }
}