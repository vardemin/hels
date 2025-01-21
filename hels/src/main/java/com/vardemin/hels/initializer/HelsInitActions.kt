package com.vardemin.hels.initializer

import android.content.Context

interface HelsInitActions {
    fun initBlocking(
        context: Context,
        initProps: Map<String, String> = emptyMap(),
        startNewSession: Boolean = true,
        port: Int = 1515
    )

    fun initAsync(
        context: Context,
        initProps: Map<String, String> = emptyMap(),
        startNewSession: Boolean = true,
        port: Int = 1515
    )

    suspend fun init(
        context: Context,
        initProps: Map<String, String> = emptyMap(),
        startNewSession: Boolean = true,
        port: Int = 1515
    )
}