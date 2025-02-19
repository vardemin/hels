package com.vardemin.hels.data

import com.vardemin.hels.data.db.dao.RequestsDao
import com.vardemin.hels.di.DataModule
import com.vardemin.hels.model.mapper.HelsLiteRequestsMapper
import com.vardemin.hels.model.mapper.HelsRequestsMapper
import com.vardemin.hels.model.request.RequestItem
import com.vardemin.hels.utils.mapEntity
import com.vardemin.hels.utils.mapItem
import com.vardemin.hels.utils.mapItemList
import com.vardemin.hels.utils.toLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.datetime.LocalDateTime
import net.gouline.kapsule.Injects
import net.gouline.kapsule.inject
import net.gouline.kapsule.required
import kotlin.coroutines.CoroutineContext

internal class RequestsDataSource(
    module: DataModule
) : Injects<DataModule>, HelsItemDataSource<RequestItem>(
    "/requests",
    "/ws/requests",
    module.json,
    RequestItem.serializer()
), CoroutineScope {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val coroutineContext: CoroutineContext = Dispatchers.Default.limitedParallelism(1)
    override val scope: CoroutineScope = this
    private val dao: RequestsDao by required { requestsDao }
    private val mapper: HelsRequestsMapper by required { requestsMapper }
    private val liteMapper: HelsLiteRequestsMapper by required { requestsLiteMapper }

    init {
        inject(module)
    }

    override suspend fun getPaginated(
        after: LocalDateTime?,
        perPage: Int
    ): List<RequestItem> {
        val items = if (after != null) {
            dao.getRequestsAfter(perPage, after.toLong())
        } else {
            dao.getRequests(perPage)
        }
        return items.mapItemList(liteMapper)
    }

    override suspend fun getById(id: String): RequestItem? {
        return dao.getRequestById(id)?.mapItem(mapper)
    }

    override suspend fun onUpdateItem(
        sessionId: String,
        id: String,
        update: (RequestItem) -> RequestItem
    ): RequestItem? {
        return getById(id)?.run(update)?.also {
            dao.updateRequests(it.mapEntity(mapper))
        }
    }

    override suspend fun onRemoveItem(sessionId: String, id: String) {
        dao.deleteById(id)
    }

    override suspend fun onReset(sessionId: String) {
        dao.clearRequestsForSession(sessionId)
    }

    override suspend fun onAddItem(sessionId: String, item: RequestItem): RequestItem {
        dao.insertRequests(item.mapEntity(mapper))
        return item
    }
}