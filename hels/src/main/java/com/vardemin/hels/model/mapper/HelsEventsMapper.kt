package com.vardemin.hels.model.mapper

import com.vardemin.hels.data.db.entity.EventEntity
import com.vardemin.hels.model.HelsMapper
import com.vardemin.hels.model.event.EventItem
import com.vardemin.hels.utils.toLocalDateTime
import com.vardemin.hels.utils.toLong

internal class HelsEventsMapper : HelsMapper<EventEntity, EventItem> {
    override fun mapItem(entity: EventEntity): EventItem {
        return with(entity) {
            EventItem(
                sessionId,
                title,
                message,
                dateTime.toLocalDateTime(),
                properties,
                id
            )
        }
    }

    override fun mapDb(item: EventItem): EventEntity {
        return with(item) {
            EventEntity(
                id,
                sessionId,
                title,
                message,
                time.toLong(),
                properties
            )
        }
    }
}