package com.vardemin.hels.data

import com.vardemin.hels.model.HelsItem
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

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
        // No op
    }

    open suspend fun persistData() {
        // No op
    }

    companion object {
        internal const val HELS_DEFAULT_REPLAY_CACHE = 256
        internal const val HELS_DEFAULT_BUFFER_CAPACITY = 1024
    }
}