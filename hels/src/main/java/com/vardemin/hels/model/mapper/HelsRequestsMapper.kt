package com.vardemin.hels.model.mapper

import com.vardemin.hels.data.db.entity.RequestEntity
import com.vardemin.hels.data.db.entity.ResponseEntity
import com.vardemin.hels.model.HelsMapper
import com.vardemin.hels.model.request.RequestItem
import com.vardemin.hels.model.request.ResponseItem
import com.vardemin.hels.utils.toLocalDateTime
import com.vardemin.hels.utils.toLong

internal class HelsRequestsMapper: HelsMapper<RequestEntity, RequestItem> {
    override fun mapItem(entity: RequestEntity): RequestItem {
        return with(entity) {
            RequestItem(
                sessionId,
                method,
                url,
                headers,
                body,
                time.toLocalDateTime(),
                response?.toResponseItem(),
                id
            )
        }
    }

    private fun ResponseEntity.toResponseItem(): ResponseItem {
        return with(this) {
            ResponseItem(
                code,
                responseHeaders,
                responseBody,
                responseTime.toLocalDateTime()
            )
        }
    }

    override fun mapDb(item: RequestItem): RequestEntity {
        return with(item) {
            RequestEntity(
                id,
                sessionId,
                method,
                url,
                headers,
                body,
                time.toLong(),
                response?.toResponseEntity()
            )
        }
    }

    private fun ResponseItem.toResponseEntity(): ResponseEntity {
        return with(this) {
            ResponseEntity(
                code,
                headers,
                body,
                time.toLong()
            )
        }
    }
}