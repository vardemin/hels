package com.vardemin.hels.log

import com.vardemin.hels.data.LogItemsDataSource
import com.vardemin.hels.data.SessionDataSource
import com.vardemin.hels.di.ComponentsModule
import com.vardemin.hels.model.log.LogItem
import com.vardemin.hels.model.log.LogLevel
import com.vardemin.hels.utils.currentDateTime
import kotlinx.datetime.LocalDateTime
import net.gouline.kapsule.Injects
import net.gouline.kapsule.inject
import net.gouline.kapsule.required

internal class LoggerImpl(
    module: ComponentsModule
) : Injects<ComponentsModule>, HLogger {

    private val sessionDataSource: SessionDataSource by required { sessionDataSource }
    private val dataSource: LogItemsDataSource by required { logItemsDataSource }

    private val sessionId get() = sessionDataSource.currentSession?.id ?: ""
    private val globalProperties get() = sessionDataSource.currentSession?.properties ?: emptyMap()

    init {
        inject(module)
    }

    override fun d(tag: String, message: String, properties: Map<String, String>) {
        pushLog(LogLevel.Debug, tag, message, properties)
    }

    override fun v(tag: String, message: String, properties: Map<String, String>) {
        pushLog(LogLevel.Verbose, tag, message, properties)
    }

    override fun i(tag: String, message: String, properties: Map<String, String>) {
        pushLog(LogLevel.Info, tag, message, properties)
    }

    private fun pushLog(
        level: LogLevel,
        title: String,
        message: String,
        props: Map<String, String>
    ) {
        dataSource.add(
            sessionId,
            LogItem(sessionId, title, message, getLocalDateTime(), level, globalProperties + props)
        )
    }

    private fun getLocalDateTime(): LocalDateTime {
        return currentDateTime()
    }
}