package com.vardemin.hels.data

import androidx.datastore.core.DataStore
import com.vardemin.hels.model.log.LogItem
import com.vardemin.hels.utils.getNativePath
import com.vardemin.hels.utils.nowInstant
import com.vardemin.hels.utils.plus
import kotlinx.coroutines.flow.firstOrNull
import okio.FileSystem
import okio.SYSTEM
import kotlin.time.Duration

internal class LogItemsDataSource(
    private val fileName: String,
    private val cacheDuration: Duration,
) : HelsDataSource<LogItem>("/logs") {

    private val dataStore: DataStore<List<LogItem>?> by lazy {
        GenericJsonDataStore(FileSystem.SYSTEM) { getNativePath(fileName) }
    }

    override suspend fun loadInitialData() {
        dataStore.data.firstOrNull()?.let {
            it.forEach { item ->
                emit(item)
            }
        }
        super.loadInitialData()
    }

    override suspend fun persistData() {
        dataStore.updateData {
            val now = nowInstant()
            this.replayCache.filter { it.dateTime.plus(cacheDuration) > now }
        }
        super.persistData()
    }
}