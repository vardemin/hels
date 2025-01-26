package com.vardemin.hels.initializer

import android.content.Context
import androidx.startup.Initializer

class HelsStartupInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        val configuration = if (context is HelsConfigurationProvider) {
            context.getHelsConfiguration()
        } else HelsConfiguration()
        HelsInitializer.initAsync(
            context,
            configuration
        )
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}