package com.vardemin.hels.model.mapper

import com.vardemin.hels.data.db.entity.SessionEntity
import com.vardemin.hels.model.HelsMapper
import com.vardemin.hels.model.session.SessionItem
import com.vardemin.hels.utils.toLocalDateTime
import com.vardemin.hels.utils.toLong

internal class HelsSessionMapper: HelsMapper<SessionEntity, SessionItem> {
    override fun mapItem(entity: SessionEntity): SessionItem {
        return with(entity) {
            SessionItem(
                id,
                started.toLocalDateTime(),
                properties
            )
        }
    }

    override fun mapDb(item: SessionItem): SessionEntity {
        return with(item) {
            SessionEntity(
                id,
                started.toLong(),
                properties
            )
        }
    }
}