package com.vardemin.hels.data

import androidx.datastore.core.DataStore
import com.vardemin.hels.proto.LogEntries
import com.vardemin.hels.proto.RequestEntries
import com.vardemin.hels.utils.getNativePath
import okio.FileSystem

actual fun getLogsDataStore(fileName: String): DataStore<LogEntries> {
    return createLogsDataStore(fileSystem = FileSystem.SYSTEM, producePath = { getNativePath(fileName) })
}

actual fun getRequestsDataStore(fileName: String): DataStore<RequestEntries> {
    return createRequestsDataStore(fileSystem = FileSystem.SYSTEM, producePath = { getNativePath(fileName) })
}