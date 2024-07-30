package com.vardemin.hels.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import com.vardemin.hels.model.HelsItem
import com.vardemin.hels.proto.LogEntries
import com.vardemin.hels.proto.RequestEntries
import com.vardemin.hels.utils.logDataStoreSerializer
import com.vardemin.hels.utils.requestDataStoreSerializer
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import okio.FileSystem
import okio.Path

open class HelsDataSource<Item : HelsItem> private constructor(
    val path: String,
    mutableSharedFlow: MutableSharedFlow<Item>
) : MutableSharedFlow<Item> by mutableSharedFlow {

    constructor(
        path: String,
        replay: Int = HELS_DEFAULT_REPLAY_CACHE,
        extraBufferCapacity: Int = HELS_DEFAULT_BUFFER_CAPACITY,
        onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
    ) : this(path, MutableSharedFlow(replay, extraBufferCapacity, onBufferOverflow))

    open suspend fun loadInitialData() {
        Napier.i("Initial data loaded for source ${this::class.simpleName}", tag = "HelsDataSource")
    }

    open suspend fun persistData() {
        Napier.i("Data persisted for source ${this::class.simpleName}", tag = "HelsDataSource")
    }

    companion object {
        internal const val HELS_DEFAULT_REPLAY_CACHE = 256
        internal const val HELS_DEFAULT_BUFFER_CAPACITY = 1024
    }
}

expect fun getLogsDataStore(fileName: String): DataStore<LogEntries>
expect fun getRequestsDataStore(fileName: String): DataStore<RequestEntries>

fun createLogsDataStore(
    fileSystem: FileSystem,
    producePath: () -> Path
): DataStore<LogEntries> =
    DataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = fileSystem,
            producePath = producePath,
            serializer = logDataStoreSerializer(),
        ),
    )

fun createRequestsDataStore(
    fileSystem: FileSystem,
    producePath: () -> Path
): DataStore<RequestEntries> =
    DataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = fileSystem,
            producePath = producePath,
            serializer = requestDataStoreSerializer(),
        ),
    )