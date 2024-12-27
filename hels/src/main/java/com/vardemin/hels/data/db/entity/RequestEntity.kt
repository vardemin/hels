package com.vardemin.hels.data.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vardemin.hels.data.local.HelsEntity

@Entity("requests")
internal data class RequestEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val method: String,
    val url: String,
    val headers: Map<String, List<String>>,
    val body: String?,
    val time: Long,
    @Embedded
    val response: ResponseEntity?
) : HelsEntity

internal data class ResponseEntity(
    val code: Int,
    val responseHeaders: Map<String, List<String>>,
    val responseBody: String?,
    val responseTime: Long
)

