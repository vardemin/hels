package com.vardemin.hels.model.log

import com.vardemin.hels.model.HelsItem
import kotlinx.datetime.LocalDateTime
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
}