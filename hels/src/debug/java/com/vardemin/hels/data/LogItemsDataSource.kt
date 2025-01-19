package com.vardemin.hels.data

import com.vardemin.hels.data.db.dao.LogsDao
import com.vardemin.hels.di.DataModule
import com.vardemin.hels.model.log.LogItem
import com.vardemin.hels.model.mapper.HelsLogsMapper
import com.vardemin.hels.utils.mapEntity
import com.vardemin.hels.utils.mapItem
import com.vardemin.hels.utils.mapItemList
import com.vardemin.hels.utils.toLong
import kotlinx.datetime.LocalDateTime
import net.gouline.kapsule.Injects
import net.gouline.kapsule.inject
import net.gouline.kapsule.required
import kotlin.coroutines.CoroutineContext

internal class LogItemsDataSource(
    module: DataModule
) : Injects<DataModule>, HelsItemDataSource<LogItem>(
    "/logs",
    "/ws/logs",
    module.json,
    LogItem.serializer()
) {
    override val coroutineContext: CoroutineContext by required { defaultCoroutineContext }
    private val logsDao: LogsDao by required { logsDao }
    private val mapper: HelsLogsMapper by required { logsMapper }

    init {
        inject(module)
    }

    override suspend fun getPaginated(
        after: LocalDateTime?,
        perPage: Int
    ): List<LogItem> {
        val logItems = if (after != null) {
            logsDao.getLogsAfter(perPage, after.toLong())
        } else {
            logsDao.getLogs(perPage)
        }
        return logItems.mapItemList(mapper)
    }

    override suspend fun getById(id: String): LogItem? {
        return logsDao.getLogById(id)?.mapItem(mapper)
    }

    override suspend fun onUpdateItem(
        sessionId: String,
        id: String,
        update: (LogItem) -> LogItem
    ): LogItem? {
        return getById(id)?.run(update)?.also {
            logsDao.updateLog(it.mapEntity(mapper))
        }
    }

    override suspend fun onRemoveItem(sessionId: String, id: String) {
        logsDao.deleteById(id)
    }

    override suspend fun onReset(sessionId: String) {
        logsDao.clearLogsForSession(sessionId)
    }

    override suspend fun onAddItem(sessionId: String, item: LogItem): LogItem {
        logsDao.insertLogs(item.mapEntity(mapper))
        return item
    }
}