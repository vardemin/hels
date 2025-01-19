package com.vardemin.hels.model.mapper

import com.vardemin.hels.data.db.entity.LiteRequestEntity
import com.vardemin.hels.data.db.entity.LiteResponseEntity
import com.vardemin.hels.model.HelsMapper
import com.vardemin.hels.model.request.RequestItem
import com.vardemin.hels.model.request.ResponseItem
import com.vardemin.hels.utils.toLocalDateTime

internal class HelsLiteRequestsMapper : HelsMapper<LiteRequestEntity, RequestItem> {
    override fun mapItem(entity: LiteRequestEntity): RequestItem {
        return with(entity) {
            RequestItem(
                sessionId,
                method,
                url,
                headers,
                bodySize,
                body,
                time.toLocalDateTime(),
                response?.toResponseItem(),
                id
            )
        }
    }

    private fun LiteResponseEntity.toResponseItem(): ResponseItem {
        return with(this) {
            ResponseItem(
                code,
                responseHeaders,
                responseBodySize,
                null,
                responseTime.toLocalDateTime()
            )
        }
    }

    override fun mapDb(item: RequestItem): LiteRequestEntity {
        throw NotImplementedError()
    }
}