package com.vardemin.hels.utils

import okio.Path

expect fun deleteRecursively(directory: String)
expect fun mkDir(baseDir: String, actualDir: String): Boolean
expect fun unpackFrontendFiles(baseDir: String, actualDir: String): Boolean
expect fun getNativePath(fileName: String): Path