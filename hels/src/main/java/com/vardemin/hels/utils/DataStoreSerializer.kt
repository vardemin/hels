package com.vardemin.hels.utils

import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream

/**
 * DataStore Serializer using kotlinx-serialization.
 * Default value is null.
 */
inline fun <reified T : Any> DataStoreSerializer(): Serializer<T?> {
    return object : Serializer<T?> {
        override val defaultValue: T? = null

        override suspend fun readFrom(input: InputStream): T? {
            return if (input.isNotEmpty()) {
                runCatching {
                    Json.decodeFromStream<T>(input)
                }.getOrNull()
            } else {
                null
            }
        }

        override suspend fun writeTo(t: T?, output: OutputStream) {
            if (t != null) {
                runCatching {
                    Json.encodeToStream(t, output)
                }
            }
        }

        private fun InputStream.isNotEmpty() = available() > 0
    }
}

suspend fun <T : Any> DataStore<T?>.clear(): T? = updateData { null }
suspend fun <T : Any> DataStore<T?>.requireValue(): T = checkNotNull(data.first())