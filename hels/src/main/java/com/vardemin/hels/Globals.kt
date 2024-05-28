package com.vardemin.hels

import com.vardemin.hels.data.RequestsDataSource
import com.vardemin.hels.log.HLogger
import com.vardemin.hels.server.HServer

internal var server: HServer? = null
internal var logger: HLogger? = null
internal val requests by lazy {
    RequestsDataSource()
}
val HelsLog get() = logger!!
val HelServer get() = server!!