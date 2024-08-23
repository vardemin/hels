package com.vardemin.hels.utils

import androidx.datastore.core.okio.OkioSerializer
import com.squareup.wire.ProtoAdapter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource

inline fun <reified T : Any> DataStoreProtoSerializer(adapter: ProtoAdapter<T>): OkioSerializer<T?> {
    return object : OkioSerializer<T?> {
        override val defaultValue: T? = null

        override suspend fun readFrom(source: BufferedSource): T? {
            return if (source.isNotEmpty()) {
                runCatching {
                    adapter.decode(source)
                }.getOrNull()
            } else null
        }

        override suspend fun writeTo(t: T?, sink: BufferedSink) {
            if (t != null) {
                runCatching {
                    adapter.encode(sink, t)
                }
            }
        }

        private fun BufferedSource.isNotEmpty() = buffer.size > 0
    }
}

inline fun <reified T : Any> DataStoreJsonSerializer(): OkioSerializer<T?> {
    return object : OkioSerializer<T?> {
        override val defaultValue: T? = null

        override suspend fun readFrom(source: BufferedSource): T? {
            return if (source.isNotEmpty()) {
                runCatching {
                    Json.decodeFromString<T>(source.readUtf8())
                }.getOrNull()
            } else null
        }

        override suspend fun writeTo(t: T?, sink: BufferedSink) {
            if (t != null) {
                runCatching {
                    sink.writeUtf8(Json.encodeToString(t))
                }
            }
        }

        private fun BufferedSource.isNotEmpty() = buffer.size > 0
    }
}