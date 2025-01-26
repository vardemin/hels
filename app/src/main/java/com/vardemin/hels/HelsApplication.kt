package com.vardemin.hels

import android.app.Application
import com.vardemin.hels.initializer.HelsConfigurationProvider
import com.vardemin.hels.initializer.HelsConfiguration
import kotlin.random.Random
import kotlin.random.nextInt

class HelsApplication: Application(), HelsConfigurationProvider {

    override fun getHelsConfiguration(): HelsConfiguration {
        return HelsConfiguration(initialProperties = mapOf(
            "version" to "23.4.1",
            "version_code" to Random.nextInt(200..1000).toString()
        ))
    }
}