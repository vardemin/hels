package com.vardemin.hels.data

import com.vardemin.hels.model.LogItem
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class LogItemsDataSource {
    private val _logItemsFlow = MutableSharedFlow<LogItem>(REPLAY_CACHE, BUFFER_CAPACITY, BufferOverflow.DROP_OLDEST)
    val logItemsFlow = _logItemsFlow.asSharedFlow()

    fun pushLog(item: LogItem) {
        _logItemsFlow.tryEmit(item)
    }

    companion object {
        private const val REPLAY_CACHE = 256
        private const val BUFFER_CAPACITY = 1024
    }
}