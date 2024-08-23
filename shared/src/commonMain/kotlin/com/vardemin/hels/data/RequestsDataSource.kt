package com.vardemin.hels.data

import androidx.datastore.core.DataStore
import com.vardemin.hels.model.request.RequestItem
import com.vardemin.hels.utils.getNativePath
import com.vardemin.hels.utils.nowInstant
import com.vardemin.hels.utils.plus
import kotlinx.coroutines.flow.firstOrNull
import okio.FileSystem
import okio.SYSTEM
import kotlin.time.Duration

internal class RequestsDataSource(
    private val fileName: String,
    private val cacheDuration: Duration,
) : HelsDataSource<RequestItem>("/requests") {
    private val dataStore: DataStore<List<RequestItem>?> by lazy {
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
            this.replayCache.filter { it.request.time.plus(cacheDuration) > now }
        }
        super.persistData()
    }
}