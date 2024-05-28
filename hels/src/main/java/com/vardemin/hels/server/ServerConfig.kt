package com.vardemin.hels.server

import com.vardemin.hels.data.HelsDataSource
import java.io.File

internal class ServerConfig(
    val port: Int = 1515,
    val frontDirectory: File,
    val dataSources: List<HelsDataSource<*>> = listOf()
)