package com.vardemin.hels.utils

import okio.FileSystem

actual val fileSystem: FileSystem
    get() = FileSystem.SYSTEM