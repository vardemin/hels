package com.vardemin.hels

import com.vardemin.hels.command.InternalCommandHandler
import com.vardemin.hels.log.LoggerCommand
import com.vardemin.hels.network.NetworkLoggerCommand
import com.vardemin.hels.server.HServer

internal var serverInstance: HServer? = null
internal var loggerInstance: InternalCommandHandler<LoggerCommand>? = null
internal var networkLoggerInstance: InternalCommandHandler<NetworkLoggerCommand>? = null