package com.vardemin.hels.network

import com.vardemin.hels.data.RequestsDataSource
import com.vardemin.hels.data.SessionDataSource
import com.vardemin.hels.di.ComponentsModule
import com.vardemin.hels.model.request.RequestItem
import com.vardemin.hels.model.request.ResponseItem
import kotlinx.datetime.LocalDateTime
import net.gouline.kapsule.Injects
import net.gouline.kapsule.inject
import net.gouline.kapsule.required

internal class NetworkLoggerImpl(
    module: ComponentsModule
) : Injects<ComponentsModule>, HNetworkLogger {

    private val sessionDataSource: SessionDataSource by required { sessionDataSource }
    private val requestsDataSource: RequestsDataSource by required { requestsDataSource }

    private val sessionId: String get() = sessionDataSource.currentSession?.id ?: ""

    init {
        inject(module)
    }

    override fun logRequest(
        method: String,
        url: String,
        headers: Map<String, List<String>>,
        bodySize: Long,
        bodyString: String?,
        time: LocalDateTime
    ): String {
        val currentSessionId = sessionId
        val requestItem = RequestItem(
            currentSessionId,
            method,
            url,
            headers,
            bodySize,
            bodyString,
            time,
            null
        )
        requestsDataSource.add(
            currentSessionId,
            requestItem
        )
        return requestItem.id
    }

    override fun logResponse(
        requestId: String,
        code: Int,
        headers: Map<String, List<String>>,
        bodySize: Long,
        bodyString: String?,
        time: LocalDateTime
    ) {
        requestsDataSource.update(sessionId, requestId) {
            it.copy(
                response = ResponseItem(code, headers, bodySize, bodyString, time)
            )
        }
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
        val currentSessionId = sessionId
        val item = RequestItem(
            currentSessionId,
            method,
            url,
            headers,
            bodySize,
            bodyString,
            time,
            ResponseItem(
                code,
                responseHeaders,
                responseBodySize,
                responseBody,
                responseTime
            )
        )
        requestsDataSource.add(currentSessionId, item)
        return item.id
    }
}