package com.vardemin.hels.utils

import com.vardemin.hels.proto.LogEntries
import androidx.datastore.core.DataStore
import androidx.datastore.core.okio.OkioSerializer
import com.vardemin.hels.proto.RequestEntries
import kotlinx.coroutines.flow.first
import okio.BufferedSink
import okio.BufferedSource
import okio.IOException

suspend fun <T : Any> DataStore<T?>.clear(): T? = updateData { null }
suspend fun <T : Any> DataStore<T?>.requireValue(): T = checkNotNull(data.first())

fun logDataStoreSerializer(): OkioSerializer<LogEntries> {
    return object : OkioSerializer<LogEntries> {
        override val defaultValue: LogEntries
            get() = LogEntries()

        override suspend fun readFrom(source: BufferedSource): LogEntries =
            try {
                LogEntries.ADAPTER.decode(source)
            } catch (exception: IOException) {
                throw Exception(exception.message ?: "Serialization Exception")
            }

        override suspend fun writeTo(t: LogEntries, sink: BufferedSink) {
            sink.write(t.encode())
        }

        private fun BufferedSource.isNotEmpty() = buffer.size > 0
    }
}

fun requestDataStoreSerializer(): OkioSerializer<RequestEntries> {
    return object : OkioSerializer<RequestEntries> {
        override val defaultValue: RequestEntries
            get() = RequestEntries()

        override suspend fun readFrom(source: BufferedSource): RequestEntries =
            try {
                RequestEntries.ADAPTER.decode(source)
            } catch (exception: IOException) {
                throw Exception(exception.message ?: "Serialization Exception")
            }

        override suspend fun writeTo(t: RequestEntries, sink: BufferedSink) {
            sink.write(t.encode())
        }

        private fun BufferedSource.isNotEmpty() = buffer.size > 0
    }
}