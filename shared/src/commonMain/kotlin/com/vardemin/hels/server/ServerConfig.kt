package com.vardemin.hels.server

import com.vardemin.hels.data.HelsDataSource

internal class ServerConfig(
    val port: Int = 1515,
    val frontDirectory: String,
    val dataSources: List<HelsDataSource<*>> = listOf()
)