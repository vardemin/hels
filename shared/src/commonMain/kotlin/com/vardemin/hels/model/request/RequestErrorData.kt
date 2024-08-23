package com.vardemin.hels.model.request

import com.vardemin.hels.proto.ErrorEntry
import com.vardemin.hels.proto.HeaderValues
import com.vardemin.hels.proto.RequestParamsEntry
import com.vardemin.hels.utils.parseLocalDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable

@Serializable
data class RequestErrorData(
    val message: String,
    val time: LocalDateTime
) {
    fun toErrorEntry(): ErrorEntry =
        ErrorEntry(
            message = message,
            millis = time.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        )

    companion object {
        fun from(errorEntry: ErrorEntry): RequestErrorData =
            RequestErrorData(
                message = errorEntry.message,
                time = parseLocalDateTime(errorEntry.millis),
            )
    }
}