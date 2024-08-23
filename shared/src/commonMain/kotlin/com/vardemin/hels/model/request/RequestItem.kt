package com.vardemin.hels.model.request

import com.vardemin.hels.model.HelsItem
import com.vardemin.hels.proto.RequestEntries
import com.vardemin.hels.proto.RequestEntry
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class RequestItem(
    val id: String,
    val request: RequestData,
    val response: ResponseData?,
    val error: RequestErrorData?
) : Comparable<RequestItem>, HelsItem {
    override fun compareTo(other: RequestItem): Int {
        return request.time.compareTo(other.request.time)
    }

    fun toCallItem(): RequestCallItem {
        return RequestCallItem(
            id,
            request.method,
            request.url,
            response?.code ?: -1,
            request.time,
            response?.time ?: error?.time,
            error?.message
        )
    }

    override fun toJson(json: Json): String {
        return json.encodeToString(this.toCallItem())
    }

    companion object {
        fun from(requestEntry: RequestEntry): RequestItem {
            if (requestEntry.request == null) {
                throw IllegalStateException("RequestEntry.request was null")
            }
            return RequestItem(
                id = requestEntry.id,
                request = RequestData.from(requestEntry.request),
                response = requestEntry.response?.let { ResponseData.from(it) },
                error = requestEntry.error?.let { RequestErrorData.from(it) },
            )
        }
    }
}

fun List<RequestItem>.toRequestEntries(): RequestEntries =
    RequestEntries(
        items = this.map {
            RequestEntry(
                id = it.id,
                request = it.request.toRequestParamsEntry(),
                response = it.response?.toResponseParamsEntry(),
                error = it.error?.toErrorEntry()
            )
        }
    )