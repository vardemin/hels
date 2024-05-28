package com.vardemin.hels.initializer

import android.content.Context
import com.vardemin.hels.HelServer
import com.vardemin.hels.data.HelsDataSource
import com.vardemin.hels.data.LogItemsDataSource
import com.vardemin.hels.log.LoggerImpl
import com.vardemin.hels.logger
import com.vardemin.hels.migration.HelsMigrator
import com.vardemin.hels.requests
import com.vardemin.hels.server
import com.vardemin.hels.server.ServerConfig
import com.vardemin.hels.server.ServerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.concurrent.Executors

object HelsInitializer {
    private const val FRONT_VERSION = 0

    private val scope = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())

    operator fun invoke(
        context: Context,
        port: Int = 1515,
        vararg additionalDataSources: HelsDataSource<*>
    ) {
        val directory = context.filesDir.resolve("hels_front")
        val actualFrontDirectory = HelsMigrator.migrate(context, directory, FRONT_VERSION)
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            useAlternativeNames = true
        }
        val loggerDataSource = LogItemsDataSource()
        logger = LoggerImpl(loggerDataSource)
        val dataSources = listOf(loggerDataSource, requests) + additionalDataSources
        val serverConfig = ServerConfig(port, actualFrontDirectory, dataSources)
        server = ServerImpl(serverConfig, json)
        scope.launch {
            HelServer.start()
        }
    }
}