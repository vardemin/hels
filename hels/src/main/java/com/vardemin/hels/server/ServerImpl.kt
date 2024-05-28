package com.vardemin.hels.server

import android.util.Log
import com.vardemin.hels.requests
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticFiles
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.serialization.json.Json

internal class ServerImpl(
    config: ServerConfig,
    json: Json
) : HServer {
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
            install(CORS) {
                anyHost()
            }

            routing {
                staticFiles("/", config.frontDirectory) {
                    extensions("html", "htm")
                }
                route("/api/v1") {
                    get("/requests/{id}") {
                        val id = call.parameters["id"] ?: ""
                        requests.replayCache.asReversed().find { it.id == id }?.let {
                            call.respond(it)
                        } ?: call.respond(HttpStatusCode.NotFound)
                    }
                }
                config.dataSources.forEach { dataSource ->
                    webSocket(dataSource.path) {
                        try {
                            dataSource.collect {
                                val jsonEncoded = it.toJson(json)
                                send(Frame.Text(jsonEncoded))
                            }
                        } catch (e: Exception) {
                            val errorMessage = e.message ?: "Error"
                            close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, errorMessage))
                            Log.d("HELS", errorMessage)
                        }
                    }
                }
            }
        }
    }

    override fun start() {
        server.start(wait = true)
    }

    override fun stop() {
        server.stop(GRACE_PERIOD_MILLIS, TIMEOUT_MILLIS)
    }

    private companion object {
        private const val GRACE_PERIOD_MILLIS = 1000L
        private const val TIMEOUT_MILLIS = 2000L
    }
}