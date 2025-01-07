package com.vardemin.hels.initializer

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.vardemin.hels.Hels
import com.vardemin.hels.data.SessionDataSource.Companion.SESSION_DEVICE_ID
import com.vardemin.hels.data.SessionDataSource.Companion.SESSION_PROP_DEVICE
import com.vardemin.hels.data.SessionDataSource.Companion.SESSION_PROP_OS
import com.vardemin.hels.di.ComponentsModule
import com.vardemin.hels.di.DataModule
import com.vardemin.hels.log.LoggerImpl
import com.vardemin.hels.loggerInstance
import com.vardemin.hels.migration.HelsMigrator
import com.vardemin.hels.network.NetworkLoggerImpl
import com.vardemin.hels.networkLoggerInstance
import com.vardemin.hels.server.ServerImpl
import com.vardemin.hels.serverInstance
import kotlinx.coroutines.runBlocking

object HelsInitializer {
    private const val FRONT_VERSION = 0

    fun initBlocking(
        context: Context,
        initProps: Map<String, String> = emptyMap(),
        startNewSession: Boolean = true,
        port: Int = 1515
    ) {
        runBlocking {
            init(context, initProps, startNewSession, port)
        }
    }

    suspend fun init(
        context: Context,
        initProps: Map<String, String> = emptyMap(),
        startNewSession: Boolean = true,
        port: Int = 1515
    ) {
        val frontDirectory = context.filesDir.resolve("hels_front")
        val actualFrontDirectory = HelsMigrator.migrate(context, frontDirectory, FRONT_VERSION)
        val dataModule = DataModule(context, port, actualFrontDirectory)
        val componentsModule = ComponentsModule(dataModule)
        val finalProps = initProps + mapOf(
            SESSION_PROP_DEVICE to ("${Build.MANUFACTURER} ${Build.MODEL}"),
            SESSION_PROP_OS to ("Android ${Build.VERSION.RELEASE}"),
            SESSION_DEVICE_ID to Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        )
        if (startNewSession) {
            componentsModule.sessionDataSource.startNewSession(finalProps)
        } else {
            componentsModule.sessionDataSource.applyLastSession(finalProps)
        }
        loggerInstance = LoggerImpl(componentsModule)
        networkLoggerInstance = NetworkLoggerImpl(componentsModule)
        serverInstance = ServerImpl(componentsModule)
        Hels.start()
    }
}