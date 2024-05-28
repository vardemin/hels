package com.vardemin.hels.model.request

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
internal data class RequestDetailItem(
    val request: RequestDetailData,
    val response: ResponseDetailData?,
    val errorMessage: String?,
    val id: String
)

@Serializable
internal data class RequestDetailData(
    val method: String,
    val url: String,
    val headers: Map<String, String>,
    val payload: String?,
    val time: LocalDateTime
)

@Serializable
internal data class ResponseDetailData(
    val code: Int,
    val headers: Map<String, String>,
    val payload: String?,
    val time: LocalDateTime
)