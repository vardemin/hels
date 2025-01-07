package com.vardemin.hels

import android.app.Application
import com.vardemin.hels.initializer.HelsInitializer
import kotlin.random.Random
import kotlin.random.nextInt

class HelsApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        HelsInitializer.initBlocking(this, mapOf(
            "version" to "23.4.1",
            "version_code" to Random.nextInt(200..1000).toString()
        ))
    }
}