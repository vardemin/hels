package com.vardemin.hels.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vardemin.hels.data.local.HelsEntity

@Entity("events")
internal data class EventEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val title: String,
    val message: String,
    val dateTime: Long,
    val properties: Map<String, String>
)  : HelsEntity