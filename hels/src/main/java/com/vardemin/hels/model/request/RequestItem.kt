package com.vardemin.hels.model.request

import com.vardemin.hels.model.HelsItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
internal data class RequestItem(
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
}