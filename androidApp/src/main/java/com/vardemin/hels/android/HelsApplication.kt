package com.vardemin.hels.android

import android.app.Application
import com.vardemin.hels.ContextHolder
import com.vardemin.hels.initializer.HelsInitializer

class HelsApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        ContextHolder.setContext(this)
        HelsInitializer()
    }
}