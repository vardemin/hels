package com.vardemin.hels.model.request

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import okhttp3.RequestBody

@Serializable
internal data class ResponseData(
    val code: Int,
    val headers: Map<String, List<String>>,
    val payload: String?,
    val time: LocalDateTime
)
