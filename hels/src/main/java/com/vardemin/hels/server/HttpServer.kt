package com.vardemin.hels.server

import com.vardemin.hels.data.LogItemsDataSource
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class HttpServer(
    config: ServerConfig,
    json: Json,
    logItemsDataSource: LogItemsDataSource
) {
    private val server by lazy {
        embeddedServer(CIO, port = config.port) {
            install(WebSockets)
            install(ContentNegotiation) {
                json(json)
            }

            install(StatusPages) {
                exception<Throwable> { call, cause ->
                    call.respond(HttpStatusCode.InternalServerError, cause.localizedMessage)
                }
            }
            routing {
                get("/") {
                    call.respondBytes(contentType = ContentType.Text.Html) {
                        config.indexBytes
                    }
                }
                get("/bulma.css") {
                    call.respondBytes(contentType = ContentType.Text.CSS) {
                        config.cssBytes
                    }
                }
                webSocket("/logs") {
                    val currentLogs = logItemsDataSource.logItemsFlow.replayCache

                    // Send initial paginated logs to the client
                    val initialLogsJson = json.encodeToString(currentLogs)
                    send(Frame.Text(initialLogsJson))

                    logItemsDataSource.logItemsFlow.collect {
                        val logJson = json.encodeToString(it)
                        send(Frame.Text(logJson))
                    }
                }
            }
        }
    }

    fun start() {
        server.start(wait = true)
    }

    fun stop() {
        server.stop(GRACE_PERIOD_MILLIS, TIMEOUT_MILLIS)
    }

    private companion object {
        private const val GRACE_PERIOD_MILLIS = 1000L
        private const val TIMEOUT_MILLIS = 2000L
    }
}