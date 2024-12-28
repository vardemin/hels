package com.vardemin.hels.model.log

import com.vardemin.hels.model.HelsItemWithSession
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class LogItem(
    override val sessionId: String,
    val title: String,
    val message: String,
    val dateTime: LocalDateTime,
    val level: LogLevel,
    val properties: Map<String, String>,
    override val id: String = UUID.randomUUID().toString(),
) : Comparable<LogItem>, HelsItemWithSession() {
    override fun compareTo(other: LogItem): Int {
        return dateTime.compareTo(other.dateTime)
    }
}