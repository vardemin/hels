package com.vardemin.hels

object Hels {
    val logger get() = HelsLog

    fun start() {
        HelServer.start()
    }

    fun stop() {
        HelServer.stop()
    }
}