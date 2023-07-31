package com.vardemin.hels.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
internal data class LogItem(
    val title: String,
    val message: String,
    val dateTime: LocalDateTime,
    val level: LogLevel,
    val properties: Map<String, String>,
) : Comparable<LogItem> {
    override fun compareTo(other: LogItem): Int {
        return dateTime.compareTo(other.dateTime)
    }
}