package com.vardemin.hels

import com.vardemin.hels.data.RequestsDataSource
import com.vardemin.hels.log.HLogger
import com.vardemin.hels.log.HRequestLogger
import com.vardemin.hels.server.HServer

internal var server: HServer? = null
internal var logger: HLogger? = null
internal var requestLogger: HRequestLogger? = null
internal var requestsDataSource: RequestsDataSource? = null
internal val requests get() = requestsDataSource!!

val HelsLog get() = logger!!
val HelServer get() = server!!
val HelsRequest get() = requestLogger!!

expect val isDebug: Boolean