package com.vardemin.hels.data

import androidx.datastore.core.DataStore
import com.vardemin.hels.model.log.LogItem
import com.vardemin.hels.model.log.toLogEntries
import com.vardemin.hels.proto.LogEntries
import com.vardemin.hels.utils.nowInstant
import com.vardemin.hels.utils.plus
import kotlinx.coroutines.flow.firstOrNull
import kotlin.time.Duration

internal class LogItemsDataSource(
    private val fileName: String,
    private val cacheDuration: Duration,
) : HelsDataSource<LogItem>("/logs") {

    private val dataStore: DataStore<LogEntries> by lazy {
        getLogsDataStore(fileName)
    }

    override suspend fun loadInitialData() {
        dataStore.data.firstOrNull()?.let {
            it.items.forEach { item ->
                emit(LogItem.from(item))
            }
        }
        super.loadInitialData()
    }

    override suspend fun persistData() {
        dataStore.updateData {
            val now = nowInstant()
            this.replayCache
                .filter { it.dateTime.plus(cacheDuration) > now }
                .toLogEntries()
        }
        super.persistData()
    }
}