package com.vardemin.hels.model.request

import com.vardemin.hels.proto.HeaderValues
import com.vardemin.hels.proto.ResponseParamsEntry
import com.vardemin.hels.utils.parseLocalDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable

@Serializable
data class ResponseData(
    val code: Int,
    val headers: Map<String, List<String>>,
    val payload: String?,
    val time: LocalDateTime
) {
    fun toResponseParamsEntry(): ResponseParamsEntry =
        ResponseParamsEntry(
            code = code,
            headers = headers.map { (key, value) -> key to HeaderValues(value) }.toMap(),
            payload = payload,
            millis = time.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        )

    companion object {
        fun from(responseParamsEntry: ResponseParamsEntry): ResponseData =
            ResponseData(
                code = responseParamsEntry.code,
                headers = responseParamsEntry.headers.map { (key, value) -> key to value.value_ }.toMap(),
                payload = responseParamsEntry.payload,
                time = parseLocalDateTime(responseParamsEntry.millis),
            )
    }
}
