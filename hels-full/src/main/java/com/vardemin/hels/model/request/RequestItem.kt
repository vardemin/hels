package com.vardemin.hels.model.request

import com.vardemin.hels.model.HelsItemWithSession
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
internal data class RequestItem(
    override val sessionId: String,
    val method: String,
    val url: String,
    val headers: Map<String, List<String>>,
    val bodySize: Long,
    val body: String?,
    override val time: LocalDateTime,
    val response: ResponseItem?,
    override val id: String = UUID.randomUUID().toString(),
) : HelsItemWithSession()

@Serializable
internal data class ResponseItem(
    val code: Int,
    val headers: Map<String, List<String>>,
    val bodySize: Long,
    val body: String?,
    val time: LocalDateTime
)