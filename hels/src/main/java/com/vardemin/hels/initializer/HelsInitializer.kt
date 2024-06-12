package com.vardemin.hels.initializer

import android.content.Context
import com.vardemin.hels.HelServer
import com.vardemin.hels.data.HelsDataSource
import com.vardemin.hels.data.LogItemsDataSource
import com.vardemin.hels.data.RequestsDataSource
import com.vardemin.hels.log.LoggerImpl
import com.vardemin.hels.logger
import com.vardemin.hels.migration.HelsMigrator
import com.vardemin.hels.requests
import com.vardemin.hels.requestsDataSource
import com.vardemin.hels.server
import com.vardemin.hels.server.ServerConfig
import com.vardemin.hels.server.ServerImpl
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.hours

object HelsInitializer {
    private const val FRONT_VERSION = 0
    private const val CACHE_HOURS = 8

    operator fun invoke(
        context: Context,
        port: Int = 1515,
        cachedHours: Int = CACHE_HOURS,
        vararg additionalDataSources: HelsDataSource<*>
    ) {
        val frontDirectory = context.filesDir.resolve("hels_front")
        val actualFrontDirectory = HelsMigrator.migrate(context, frontDirectory, FRONT_VERSION)
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            useAlternativeNames = true
        }
        val dbDirectory = context.filesDir.resolve("hels_db")
        val cachingDuration = cachedHours.hours
        val loggerDataSource = LogItemsDataSource(dbDirectory, cachingDuration)
        requestsDataSource = RequestsDataSource(dbDirectory, cachingDuration)
        logger = LoggerImpl(loggerDataSource)
        val dataSources = listOf(loggerDataSource, requests) + additionalDataSources
        val serverConfig = ServerConfig(port, actualFrontDirectory, dataSources)
        server = ServerImpl(serverConfig, json)
        HelServer.start()
    }
}