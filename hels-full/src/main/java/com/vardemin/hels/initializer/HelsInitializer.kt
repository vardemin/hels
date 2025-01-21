package com.vardemin.hels.initializer

import android.content.Context
import android.os.Build
import com.vardemin.hels.Hels
import com.vardemin.hels.data.SessionDataSource.Companion.SESSION_PROP_DEVICE
import com.vardemin.hels.data.SessionDataSource.Companion.SESSION_PROP_OS
import com.vardemin.hels.di.ComponentsModule
import com.vardemin.hels.di.DataModule
import com.vardemin.hels.log.LoggerCommandHandler
import com.vardemin.hels.loggerInstance
import com.vardemin.hels.migration.HelsMigrator
import com.vardemin.hels.network.NetworkLoggerCommandHandler
import com.vardemin.hels.networkLoggerInstance
import com.vardemin.hels.server.ServerImpl
import com.vardemin.hels.serverInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

object HelsInitializer : HelsInitActions {
    private const val FRONT_VERSION = 1

    private val coroutineScope: CoroutineScope
        get() = CoroutineScope(
            SupervisorJob() + Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        )

    override fun initBlocking(
        context: Context,
        initProps: Map<String, String>,
        startNewSession: Boolean,
        port: Int
    ) {
        runBlocking {
            init(context, initProps, startNewSession, port)
        }
    }

    override fun initAsync(
        context: Context,
        initProps: Map<String, String>,
        startNewSession: Boolean,
        port: Int
    ) {
        coroutineScope.launch {
            init(context, initProps, startNewSession, port)
        }
    }

    override suspend fun init(
        context: Context,
        initProps: Map<String, String>,
        startNewSession: Boolean,
        port: Int
    ) {
        val frontDirectory = context.filesDir.resolve("hels_front")
        val actualFrontDirectory = HelsMigrator.migrate(context, frontDirectory, FRONT_VERSION)
        val dataModule = DataModule(context, port, actualFrontDirectory)
        val componentsModule = ComponentsModule(dataModule)
        val finalProps = initProps + mapOf(
            SESSION_PROP_DEVICE to ("${Build.MANUFACTURER} ${Build.MODEL}"),
            SESSION_PROP_OS to ("Android ${Build.VERSION.RELEASE}")
        )
        if (startNewSession) {
            componentsModule.sessionDataSource.startNewSession(finalProps)
        } else {
            componentsModule.sessionDataSource.applyLastSession(finalProps)
        }
        loggerInstance = LoggerCommandHandler(componentsModule)
        networkLoggerInstance = NetworkLoggerCommandHandler(componentsModule)
        serverInstance = ServerImpl(componentsModule)
        Hels.start()
    }
}