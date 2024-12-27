package com.vardemin.hels.server

import com.vardemin.hels.data.HelsItemDataSource
import com.vardemin.hels.data.SessionDataSource
import java.io.File

internal class ServerConfig(
    val port: Int = 1515,
    val frontDirectory: File,
    val sessionDataSource: SessionDataSource,
    val itemDataSources: List<HelsItemDataSource<*>>
)