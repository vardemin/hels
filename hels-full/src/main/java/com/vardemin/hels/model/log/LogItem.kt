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
    override val time: LocalDateTime,
    val level: LogLevel,
    override val id: String = UUID.randomUUID().toString(),
) : HelsItemWithSession()