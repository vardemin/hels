package com.vardemin.hels.model.request

import com.vardemin.hels.proto.HeaderValues
import com.vardemin.hels.proto.RequestParamsEntry
import com.vardemin.hels.utils.parseLocalDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable

@Serializable
data class RequestData(
    val method: String,
    val url: String,
    val headers: Map<String, List<String>>,
    val payload: String?,
    val time: LocalDateTime
) {
    fun toRequestParamsEntry(): RequestParamsEntry =
        RequestParamsEntry(
            method = method,
            url = url,
            headers = headers.map { (key, value) -> key to HeaderValues(value) }.toMap(),
            payload = payload,
            millis = time.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        )

    companion object {
        fun from(requestParamsEntry: RequestParamsEntry): RequestData =
            RequestData(
                method = requestParamsEntry.method,
                url = requestParamsEntry.url,
                headers = requestParamsEntry.headers.map { (key, value) -> key to value.value_ }.toMap(),
                payload = requestParamsEntry.payload,
                time = parseLocalDateTime(requestParamsEntry.millis),
            )
    }
}