package com.vardemin.hels

import com.vardemin.hels.log.HLogger
import com.vardemin.hels.network.HNetworkLogger
import com.vardemin.hels.server.HServer

internal var serverInstance: HServer? = null
internal var loggerInstance: HLogger? = null
internal var networkLoggerInstance: HNetworkLogger? = null