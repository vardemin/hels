package com.vardemin.hels.data.db.entity

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.vardemin.hels.data.local.HelsEntity

@DatabaseView("SELECT id, sessionId, method, url, headers, bodySize, body, time, code, " +
        "responseHeaders, responseBodySize, responseTime FROM requests", "lite_requests")
internal data class LiteRequestEntity(
    val id: String,
    val sessionId: String,
    val method: String,
    val url: String,
    val headers: Map<String, List<String>>,
    val bodySize: Long,
    val body: String?,
    val time: Long,
    @Embedded
    val response: LiteResponseEntity?
) : HelsEntity

internal data class LiteResponseEntity(
    val code: Int,
    val responseHeaders: Map<String, List<String>>,
    val responseBodySize: Long,
    val responseTime: Long
)