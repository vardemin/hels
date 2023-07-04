package com.vardemin.hels.model

import kotlinx.serialization.Serializable

@Serializable
data class HLog(
    val level: HLogLevel
)