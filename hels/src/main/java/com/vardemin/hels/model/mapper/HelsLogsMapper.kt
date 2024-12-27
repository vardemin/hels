package com.vardemin.hels.model.mapper

import com.vardemin.hels.data.db.entity.LogItemEntity
import com.vardemin.hels.model.HelsMapper
import com.vardemin.hels.model.log.LogItem
import com.vardemin.hels.model.mapper.LogLevelMapper.mapEntity
import com.vardemin.hels.model.mapper.LogLevelMapper.mapItem
import com.vardemin.hels.utils.toLocalDateTime
import com.vardemin.hels.utils.toLong

internal class HelsLogsMapper: HelsMapper<LogItemEntity, LogItem> {
    override fun mapItem(entity: LogItemEntity): LogItem {
        return with(entity) {
            LogItem(
                sessionId,
                title,
                message,
                dateTime.toLocalDateTime(),
                level.mapItem(),
                properties,
                id
            )
        }
    }

    override fun mapDb(item: LogItem): LogItemEntity {
        return with(item) {
            LogItemEntity(
                id,
                sessionId,
                title,
                message,
                dateTime.toLong(),
                level.mapEntity(),
                properties
            )
        }
    }
}