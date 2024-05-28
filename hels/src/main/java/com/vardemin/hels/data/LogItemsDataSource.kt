package com.vardemin.hels.data

import com.vardemin.hels.model.log.LogItem
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class LogItemsDataSource : HelsDataSource<LogItem>("/logs")