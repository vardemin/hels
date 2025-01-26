package com.vardemin.hels.initializer

import com.vardemin.hels.HELS_DEFAULT_PORT

class HelsConfiguration(
    val startNewSession: Boolean = true,
    val port: Int = HELS_DEFAULT_PORT,
    val initialProperties: Map<String, String> = emptyMap(),
    val customFrontendVersion: Int? = null
)