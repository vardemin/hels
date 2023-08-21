package com.vardemin.hels

import com.vardemin.hels.log.HelsLogger
import com.vardemin.hels.server.HttpServer

object Hels {
    private var logger: HelsLogger? = null
    val Log: HelsLogger get() = logger!!

    internal fun init(logger: HelsLogger) {
        this.logger = logger
    }

}