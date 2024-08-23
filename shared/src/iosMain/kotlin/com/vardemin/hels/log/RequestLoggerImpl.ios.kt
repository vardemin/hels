package com.vardemin.hels.log

import platform.Foundation.NSUUID

actual fun randomUUID(): String = NSUUID().UUIDString()