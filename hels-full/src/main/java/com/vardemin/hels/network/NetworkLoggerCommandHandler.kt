package com.vardemin.hels.network

import android.util.Log
import com.vardemin.hels.command.InternalCommandHandler
import com.vardemin.hels.data.RequestsDataSource
import com.vardemin.hels.data.SessionDataSource
import com.vardemin.hels.di.ComponentsModule
import com.vardemin.hels.model.request.RequestItem
import com.vardemin.hels.model.request.ResponseItem
import com.vardemin.hels.utils.toLocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalDateTime
import net.gouline.kapsule.Injects
import net.gouline.kapsule.inject
import net.gouline.kapsule.required

internal class NetworkLoggerCommandHandler(
    module: ComponentsModule
) : Injects<ComponentsModule>, InternalCommandHandler<NetworkLoggerCommand> {

    private val sessionDataSource: SessionDataSource by required { sessionDataSource }
    private val requestsDataSource: RequestsDataSource by required { requestsDataSource }
    private val scope: CoroutineScope by required { dataModule.defaultScope }
    private val sessionId: String get() = sessionDataSource.currentSession?.id ?: ""

    init {
        inject(module)
        NetworkLogsEmitter.commandFlow.onEach {
            handle(it)
        }.catch {
            Log.e("HELS", it.localizedMessage, it)
        }.launchIn(scope)
    }

    override fun handle(command: NetworkLoggerCommand) {
        with(command) {
            when (this) {
                is NetworkLoggerCommand.LogFullRequest -> logFullRequest(
                    id,
                    method,
                    url,
                    headers,
                    bodySize,
                    bodyString,
                    time.toLocalDateTime(),
                    code,
                    responseHeaders,
                    responseBodySize,
                    responseBody,
                    time.toLocalDateTime()
                )

                is NetworkLoggerCommand.LogRequest -> logRequest(
                    id,
                    method,
                    url,
                    headers,
                    bodySize,
                    bodyString,
                    time.toLocalDateTime(),
                )

                is NetworkLoggerCommand.LogResponse -> logResponse(
                    requestId,
                    code,
                    headers,
                    bodySize,
                    bodyString,
                    time.toLocalDateTime()
                )
            }
        }
    }

    private fun logRequest(
        id: String,
        method: String,
        url: String,
        headers: Map<String, List<String>>,
        bodySize: Long,
        bodyString: String?,
        time: LocalDateTime
    ) {
        val currentSessionId = sessionId
        val requestItem = RequestItem(
            currentSessionId,
            method,
            url,
            headers,
            bodySize,
            bodyString,
            time,
            null,
            id
        )
        requestsDataSource.add(
            currentSessionId,
            requestItem
        )
    }

    private fun logResponse(
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

    private fun logFullRequest(
        id: String,
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
    ) {
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
            ),
            id
        )
        requestsDataSource.add(currentSessionId, item)
    }
}