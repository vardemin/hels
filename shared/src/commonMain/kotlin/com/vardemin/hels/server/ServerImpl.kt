package com.vardemin.hels.server

import com.vardemin.hels.requests
import com.vardemin.hels.utils.configureStaticRouting
import com.vardemin.hels.utils.getNativePath
import com.vardemin.hels.utils.ticker
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

internal class ServerImpl(
    private val config: ServerConfig,
    private val json: Json
) : HServer, CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    private var replicaJob: Job? = null

    init {
        config.dataSources.forEach {
            launch {
                it.loadInitialData()
            }
        }
        replicaJob = launch {
            ticker(REPLICA_JOB_INTERVAL) {
                launch {
                    config.dataSources.forEach {
                        it.persistData()
                    }
                }
            }
        }
    }

    private var server: CIOApplicationEngine? = null

    private fun initServer() =
        embeddedServer(CIO, port = config.port, host = "0.0.0.0") {
            install(WebSockets)
            install(ContentNegotiation) {
                json(json)
            }
            install(StatusPages) {
                exception<Throwable> { call, cause ->
                    call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
                }
            }
            install(CORS) {
                anyHost()
            }
            configureStaticRouting("/", getNativePath(config.frontDirectory))
            routing {
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
                            var message = "Error"
                            if (e is CancellationException) {
                                message = "Server stopped by user"
                            } else {
                                e.message?.let { message = it }
                                dataSource.persistData()
                                Napier.d(message, e, "ServerImpl")
                            }
                            close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, message))
                        }
                    }
                }
            }
        }


    override fun start() {
        Napier.i("Starting server...", tag = "ServerImpl")
        server = initServer()
        server?.start(wait = false)
    }

    override fun stop() {
        Napier.i("Stopping server...", tag = "ServerImpl")
        replicaJob?.cancel()
        replicaJob = null
        config.dataSources.forEach {
            launch {
                it.persistData()
            }
        }
        server?.stop(GRACE_PERIOD_MILLIS, TIMEOUT_MILLIS)
    }

    private companion object {
        private const val GRACE_PERIOD_MILLIS = 1000L
        private const val TIMEOUT_MILLIS = 2000L
        private const val REPLICA_JOB_INTERVAL = 600000L
    }
}