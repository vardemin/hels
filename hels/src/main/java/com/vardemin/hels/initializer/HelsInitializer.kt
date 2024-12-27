package com.vardemin.hels.initializer

import android.content.Context
import com.vardemin.hels.Hels
import com.vardemin.hels.data.HelsItemDataSource
import com.vardemin.hels.data.LogItemsDataSource
import com.vardemin.hels.data.RequestsDataSource
import com.vardemin.hels.data.SessionDataSource
import com.vardemin.hels.di.DataModule
import com.vardemin.hels.log.LoggerImpl
import com.vardemin.hels.loggerInstance
import com.vardemin.hels.migration.HelsMigrator
import com.vardemin.hels.network.NetworkLoggerImpl
import com.vardemin.hels.networkLoggerInstance
import com.vardemin.hels.server.ServerConfig
import com.vardemin.hels.server.ServerImpl
import com.vardemin.hels.serverInstance

object HelsInitializer {
    private const val FRONT_VERSION = 0

    operator fun invoke(
        context: Context,
        port: Int = 1515,
        vararg additionalDataSources: HelsItemDataSource<*>
    ) {
        val frontDirectory = context.filesDir.resolve("hels_front")
        val actualFrontDirectory = HelsMigrator.migrate(context, frontDirectory, FRONT_VERSION)

        val diModule = DataModule(context)
        val loggerDataSource = LogItemsDataSource(diModule)
        val requestsDataSource = RequestsDataSource(diModule)
        val allDataSources: List<HelsItemDataSource<*>> =
            listOf(loggerDataSource, requestsDataSource, *additionalDataSources)
        val sessionDataSource = SessionDataSource(diModule, allDataSources)
        loggerInstance = LoggerImpl(sessionDataSource, loggerDataSource)
        networkLoggerInstance = NetworkLoggerImpl(sessionDataSource, requestsDataSource)
        val serverConfig = ServerConfig(port, actualFrontDirectory, sessionDataSource, allDataSources)
        serverInstance = ServerImpl(serverConfig, diModule.json)
        Hels.start()
    }
}