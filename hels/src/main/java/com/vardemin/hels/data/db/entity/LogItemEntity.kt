package com.vardemin.hels.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vardemin.hels.data.local.HelsEntity

@Entity("logs")
internal data class LogItemEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val title: String,
    val message: String,
    val dateTime: Long,
    val level: LogLevelEntity,
    val properties: Map<String, String>
)  : HelsEntity

internal enum class LogLevelEntity {
    Verbose,
    Debug,
    Info,
    Warning,
    Error,
    Fatal,
    Silent
}