package com.vardemin.hels.model.log

import com.vardemin.hels.model.HelsItem
import com.vardemin.hels.proto.LogEntries
import com.vardemin.hels.proto.LogEntry
import com.vardemin.hels.proto.LogEntryLevel
import com.vardemin.hels.utils.parseLocalDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class LogItem(
    val title: String,
    val message: String,
    val dateTime: LocalDateTime,
    val level: LogLevel,
    val properties: Map<String, String>,
) : Comparable<LogItem>, HelsItem {
    override fun compareTo(other: LogItem): Int {
        return dateTime.compareTo(other.dateTime)
    }

    override fun toJson(json: Json): String {
        return json.encodeToString(this)
    }

    companion object {
        fun from(logEntry: LogEntry) =
            LogItem(
                title = logEntry.title,
                message = logEntry.message,
                dateTime = parseLocalDateTime(logEntry.millis),
                level = convertLogLevel(logEntry.level),
                properties = logEntry.properties,
            )
    }
}

fun List<LogItem>.toLogEntries(): LogEntries =
    LogEntries(
        items = this.map {
            LogEntry(
                title = it.title,
                message = it.message,
                millis = it.dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
                level = mapLogLevel(it.level),
                properties = it.properties,
            )
        }
    )

fun mapLogLevel(level: LogLevel): LogEntryLevel =
    when(level) {
        LogLevel.Info -> LogEntryLevel.Info
        LogLevel.Verbose -> LogEntryLevel.Verbose
        LogLevel.Debug -> LogEntryLevel.Debug
        LogLevel.Warning -> LogEntryLevel.Warning
        LogLevel.Error -> LogEntryLevel.Error
        LogLevel.Fatal -> LogEntryLevel.Fatal
        LogLevel.Silent -> LogEntryLevel.Silent
    }
