package com.vardemin.hels

import com.vardemin.hels.log.HLogger
import com.vardemin.hels.network.HNetworkLogger
import com.vardemin.hels.server.HServer

interface HelsFacade: HServer, HLogger, HNetworkLogger