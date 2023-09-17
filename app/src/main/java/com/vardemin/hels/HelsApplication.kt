package com.vardemin.hels

import android.app.Application
import com.vardemin.hels.initializer.HelsInitializer

class HelsApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        HelsInitializer(this)
    }
}