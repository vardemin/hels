package com.vardemin.hels.initializer

import android.content.Context

object HelsInitializer : HelsInitActions {
    override fun initBlocking(
        context: Context,
        initProps: Map<String, String>,
        startNewSession: Boolean,
        port: Int
    ) {
        // No op
    }

    override fun initAsync(
        context: Context,
        initProps: Map<String, String>,
        startNewSession: Boolean,
        port: Int
    ) {
        // No op
    }

    override suspend fun init(
        context: Context,
        initProps: Map<String, String>,
        startNewSession: Boolean,
        port: Int
    ) {
        // No op
    }
}