package com.vardemin.hels.model.request

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class RequestCallItem(
    val id: String,
    val method: String,
    val url: String,
    val status: Int,
    val start: LocalDateTime,
    val end: LocalDateTime?,
    val error: String?
)