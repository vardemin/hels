package com.vardemin.hels.data.db.entity

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.vardemin.hels.data.local.HelsEntity

@DatabaseView("SELECT id, sessionId, method, url, headers, body, time, code, " +
        "responseHeaders, responseTime FROM requests", "lite_requests")
internal data class LiteRequestEntity(
    val id: String,
    val sessionId: String,
    val method: String,
    val url: String,
    val headers: Map<String, List<String>>,
    val body: String?,
    val time: Long,
    @Embedded
    val response: LiteResponseEntity?
) : HelsEntity

internal data class LiteResponseEntity(
    val code: Int,
    val responseHeaders: Map<String, List<String>>,
    val responseTime: Long
)