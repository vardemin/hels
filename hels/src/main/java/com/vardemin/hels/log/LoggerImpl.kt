package com.vardemin.hels.log

import com.vardemin.hels.data.EventItemsDataSource
import com.vardemin.hels.data.LogItemsDataSource
import com.vardemin.hels.data.SessionDataSource
import com.vardemin.hels.di.ComponentsModule
import com.vardemin.hels.model.event.EventItem
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
    private val eventsDataSource: EventItemsDataSource by required { eventsDataSource }

    private val sessionId get() = sessionDataSource.currentSession?.id ?: ""

    init {
        inject(module)
    }

    override fun d(tag: String, message: String) {
        pushLog(LogLevel.Debug, tag, message)
    }

    override fun v(tag: String, message: String) {
        pushLog(LogLevel.Verbose, tag, message)
    }

    override fun i(tag: String, message: String) {
        pushLog(LogLevel.Info, tag, message)
    }

    override fun e(tag: String, message: String) {
        pushLog(LogLevel.Error, tag, message)
    }

    override fun e(tag: String, throwable: Throwable?) {
        pushLog(LogLevel.Error, tag, throwable?.message ?: "Some error occurred")
    }

    override fun event(title: String, message: String, properties: Map<String, String>) {
        eventsDataSource.add(
            sessionId,
            EventItem(sessionId, title, message, getLocalDateTime(), properties)
        )
    }

    override fun setAttributes(vararg attrs: Pair<String, String>) {
        sessionDataSource.updateCurrentProps {
            attrs.forEach {
                this[it.first] = it.second
            }
        }
    }

    private fun pushLog(
        level: LogLevel,
        tag: String,
        message: String
    ) {
        dataSource.add(
            sessionId,
            LogItem(sessionId, tag, message, getLocalDateTime(), level)
        )
    }

    private fun getLocalDateTime(): LocalDateTime {
        return currentDateTime()
    }
}