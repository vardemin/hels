package com.vardemin.hels

import com.vardemin.hels.log.HelsLogger
import com.vardemin.hels.server.HttpServer

internal var server: HttpServer? = null
internal var logger: HelsLogger? = null
val HelsLog get() = logger!!