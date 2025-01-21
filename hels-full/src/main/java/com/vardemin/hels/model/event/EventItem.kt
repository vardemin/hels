package com.vardemin.hels.model.event

import com.vardemin.hels.model.HelsItemWithSession
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class EventItem(
    override val sessionId: String,
    val title: String,
    val message: String,
    override val time: LocalDateTime,
    val properties: Map<String, String>,
    override val id: String = UUID.randomUUID().toString(),
) : HelsItemWithSession()