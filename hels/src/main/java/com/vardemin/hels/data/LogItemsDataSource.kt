package com.vardemin.hels.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.vardemin.hels.model.log.LogItem
import com.vardemin.hels.utils.DataStoreSerializer
import com.vardemin.hels.utils.nowInstant
import com.vardemin.hels.utils.plus
import kotlinx.coroutines.flow.firstOrNull
import java.io.File
import kotlin.time.Duration

internal class LogItemsDataSource(
    directory: File,
    private val cacheDuration: Duration,
) : HelsDataSource<LogItem>("/logs") {

    private val dataStore: DataStore<List<LogItem>?> by lazy {
        DataStoreFactory.create(
            DataStoreSerializer<List<LogItem>>()
        ) {
            File(directory, "log_items.json")
        }
    }

    override suspend fun loadInitialData() {
        dataStore.data.firstOrNull()?.let {
            it.forEach { item ->
                emit(item)
            }
        }
    }

    override suspend fun persistData() {
        dataStore.updateData {
            val now = nowInstant()
            this.replayCache.filter { it.dateTime.plus(cacheDuration) > now }
        }
    }
}