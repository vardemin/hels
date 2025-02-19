package com.vardemin.hels.server

import com.vardemin.hels.di.ComponentsModule
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticFiles
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import net.gouline.kapsule.Injects
import net.gouline.kapsule.inject
import net.gouline.kapsule.required
import kotlin.coroutines.CoroutineContext

internal class ServerImpl(
    module: ComponentsModule
) : Injects<ComponentsModule>, HServer, CoroutineScope {

    private val sessionDataSource by required { sessionDataSource }
    override val coroutineContext: CoroutineContext = Dispatchers.IO
    private val json by required { dataModule.json }
    private var replicaJob: Job? = null

    private val server by lazy {
        embeddedServer(CIO, port = module.dataModule.port, host = "0.0.0.0") {
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
                staticFiles("/", module.dataModule.frontDirectory) {
                    default("index.html")
                    extensions("html", "htm")
                }
                sessionDataSource.configureRouting(this)
                module.allDataSources.forEach {
                    it.configureRouting(this)
                }
            }
        }
    }

    init {
        inject(module)
    }

    override fun start() {
        server.start(wait = false)
    }

    override fun stop() {
        replicaJob?.cancel()
        replicaJob = null
        server.stop(GRACE_PERIOD_MILLIS, TIMEOUT_MILLIS)
    }

    private companion object {
        private const val GRACE_PERIOD_MILLIS = 1000L
        private const val TIMEOUT_MILLIS = 2000L
    }
}