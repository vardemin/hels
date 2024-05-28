package com.vardemin.hels.model.request

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
internal data class RequestErrorData(
    val message: String,
    val time: LocalDateTime
)