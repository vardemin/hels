package com.vardemin.hels.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.vardemin.hels.model.request.RequestItem
import com.vardemin.hels.utils.DataStoreSerializer
import com.vardemin.hels.utils.nowInstant
import com.vardemin.hels.utils.plus
import kotlinx.coroutines.flow.firstOrNull
import java.io.File
import kotlin.time.Duration

internal class RequestsDataSource(
    directory: File,
    private val cacheDuration: Duration,
) : HelsDataSource<RequestItem>("/requests") {
    private val dataStore: DataStore<List<RequestItem>?> by lazy {
        DataStoreFactory.create(
            DataStoreSerializer<List<RequestItem>>()
        ) {
            File(directory, "request_items.json")
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
            this.replayCache.filter { it.request.time.plus(cacheDuration) > now }
        }
    }
}