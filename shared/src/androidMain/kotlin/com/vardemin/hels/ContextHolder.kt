package com.vardemin.hels

import android.content.Context

object ContextHolder {
    private var appContext: Context? = null

    val context: Context
        get() = appContext!!

    fun setContext(context: Context) {
        appContext = context
    }
}