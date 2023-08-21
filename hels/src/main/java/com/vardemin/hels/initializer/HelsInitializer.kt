package com.vardemin.hels.initializer

import android.content.Context
import com.vardemin.hels.data.LogItemsDataSource
import com.vardemin.hels.log.LoggerImpl
import com.vardemin.hels.logger
import com.vardemin.hels.server
import com.vardemin.hels.server.HttpServer
import com.vardemin.hels.server.ServerConfig
import kotlinx.serialization.json.Json

class HelsInitializer {
    operator fun invoke(context: Context) {
        val serverConfig = ServerConfig(context)
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            useAlternativeNames = true
        }
        val dataSource = LogItemsDataSource()
        server = HttpServer(serverConfig, json, dataSource).also {
            it.start()
        }
        logger = LoggerImpl(dataSource)
    }
}