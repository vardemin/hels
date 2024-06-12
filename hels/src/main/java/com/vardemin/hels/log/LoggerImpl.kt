package com.vardemin.hels.log

import com.vardemin.hels.data.LogItemsDataSource
import com.vardemin.hels.model.log.LogItem
import com.vardemin.hels.model.log.LogLevel
import com.vardemin.hels.utils.currentDateTime
import kotlinx.datetime.LocalDateTime

internal class LoggerImpl(private val dataSource: LogItemsDataSource) : HLogger {
    private val globalProperties = mutableMapOf<String, String>()

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
        dataSource.tryEmit(
            LogItem(title, message, getLocalDateTime(), level, globalProperties + props)
        )
    }

    private fun getLocalDateTime(): LocalDateTime {
        return currentDateTime()
    }
}