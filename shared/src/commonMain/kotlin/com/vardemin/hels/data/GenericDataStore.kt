package com.vardemin.hels.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import com.squareup.wire.ProtoAdapter
import com.vardemin.hels.utils.DataStoreJsonSerializer
import com.vardemin.hels.utils.DataStoreProtoSerializer
import kotlinx.coroutines.flow.first
import okio.FileSystem
import okio.Path
import okio.SYSTEM

suspend fun <T : Any> DataStore<T?>.clear(): T? = updateData { null }
suspend fun <T : Any> DataStore<T?>.requireValue(): T = checkNotNull(data.first())

inline fun <reified T : Any> GenericProtoDataStore(
    fileSystem: FileSystem = FileSystem.SYSTEM,
    protoAdapter: ProtoAdapter<T>,
    noinline producePath: () -> Path
): DataStore<T?> = DataStoreFactory.create(
    storage = OkioStorage(
        fileSystem = fileSystem,
        producePath = producePath,
        serializer = DataStoreProtoSerializer(protoAdapter),
    ),
)

inline fun <reified T : Any> GenericJsonDataStore(
    fileSystem: FileSystem = FileSystem.SYSTEM,
    noinline producePath: () -> Path
): DataStore<T?> = DataStoreFactory.create(
    storage = OkioStorage(
        fileSystem = fileSystem,
        producePath = producePath,
        serializer = DataStoreJsonSerializer(),
    ),
)