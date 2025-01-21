package com.vardemin.hels.model.mapper

import com.vardemin.hels.data.db.entity.LogLevelEntity
import com.vardemin.hels.model.log.LogLevel

internal object LogLevelMapper {
    fun LogLevel.mapEntity(): LogLevelEntity {
        return when(this) {
            LogLevel.Verbose -> LogLevelEntity.Verbose
            LogLevel.Debug -> LogLevelEntity.Debug
            LogLevel.Info -> LogLevelEntity.Info
            LogLevel.Warning -> LogLevelEntity.Warning
            LogLevel.Error -> LogLevelEntity.Error
            LogLevel.Fatal -> LogLevelEntity.Fatal
            LogLevel.Silent -> LogLevelEntity.Silent
        }
    }

    fun LogLevelEntity.mapItem(): LogLevel {
        return when(this) {
            LogLevelEntity.Verbose -> LogLevel.Verbose
            LogLevelEntity.Debug -> LogLevel.Debug
            LogLevelEntity.Info -> LogLevel.Info
            LogLevelEntity.Warning -> LogLevel.Warning
            LogLevelEntity.Error -> LogLevel.Error
            LogLevelEntity.Fatal -> LogLevel.Fatal
            LogLevelEntity.Silent -> LogLevel.Silent
        }
    }
}