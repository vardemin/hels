package com.vardemin.hels.data

import androidx.datastore.core.DataStore
import com.vardemin.hels.model.request.RequestItem
import com.vardemin.hels.model.request.toRequestEntries
import com.vardemin.hels.proto.RequestEntries
import com.vardemin.hels.utils.nowInstant
import com.vardemin.hels.utils.plus
import kotlinx.coroutines.flow.firstOrNull
import kotlin.time.Duration

internal class RequestsDataSource(
    private val fileName: String,
    private val cacheDuration: Duration,
) : HelsDataSource<RequestItem>("/requests") {
    private val dataStore: DataStore<RequestEntries> by lazy {
        getRequestsDataStore(fileName)
    }

    override suspend fun loadInitialData() {
        dataStore.data.firstOrNull()?.let {
            it.items.forEach { item ->
                emit(RequestItem.from(item))
            }
        }
    }

    override suspend fun persistData() {
        dataStore.updateData {
            val now = nowInstant()
            this.replayCache
                .filter { it.request.time.plus(cacheDuration) > now }
                .toRequestEntries()
        }
    }
}