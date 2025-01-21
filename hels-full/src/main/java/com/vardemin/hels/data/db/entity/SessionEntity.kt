package com.vardemin.hels.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vardemin.hels.data.local.HelsEntity

@Entity("sessions")
internal data class SessionEntity(
    @PrimaryKey val id: String,
    val started: Long,
    val properties: Map<String, String>
) : HelsEntity