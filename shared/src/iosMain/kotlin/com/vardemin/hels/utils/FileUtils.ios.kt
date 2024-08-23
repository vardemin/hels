package com.vardemin.hels.utils

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.value
import okio.Path
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSNumber
import platform.Foundation.NSURL
import platform.Foundation.NSURLIsDirectoryKey
import cocoapods.SSZipArchive.SSZipArchive
import okio.Path.Companion.toPath
import platform.Foundation.NSBundle
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSUserDomainMask
import platform.Foundation.URLByAppendingPathComponent

@OptIn(ExperimentalForeignApi::class)
actual fun deleteRecursively(directory: String) {
    val fileManager = NSFileManager.defaultManager
    val url = NSURL.fileURLWithPath(NSBundle.mainBundle.resourcePath + "/$directory", true)

    memScoped {
        val errorPtr = allocPointerTo<ObjCObjectVar<NSError?>>()
        val success = deleteRecursively(url, fileManager, errorPtr.value)
        success && errorPtr.value == null
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun deleteRecursively(url: NSURL, fileManager: NSFileManager, errorPtr: CPointer<ObjCObjectVar<NSError?>>?): Boolean {
    val contents = fileManager.contentsOfDirectoryAtURL(url, includingPropertiesForKeys = null, options = 0u, error = errorPtr)
    contents?.forEach { fileUrl ->
        if ((fileUrl as NSURL).isDirectory) {
            if (!deleteRecursively(fileUrl, fileManager, errorPtr)) return false
        } else {
            if (!fileManager.removeItemAtURL(fileUrl, errorPtr)) return false
        }
    }
    return fileManager.removeItemAtURL(url, errorPtr)
}

@OptIn(ExperimentalForeignApi::class)
private val NSURL.isDirectory: Boolean
    get() {
        val resourceValues = this.resourceValuesForKeys(keys = listOf(NSURLIsDirectoryKey), error = null)
        return (resourceValues?.get(NSURLIsDirectoryKey) as? NSNumber)?.boolValue ?: false
    }

@OptIn(ExperimentalForeignApi::class)
actual fun mkDir(baseDir: String, actualDir: String): Boolean {
    val fileManager = NSFileManager.defaultManager
    val documentDirectory = fileManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    val newDirPath = documentDirectory
        ?.URLByAppendingPathComponent(baseDir, true)
        ?.URLByAppendingPathComponent(actualDir, true)

    return newDirPath?.path?.let {
        fileManager.createDirectoryAtPath(it, withIntermediateDirectories = true, attributes = null, error = null)
    } ?: false
}

@OptIn(ExperimentalForeignApi::class)
actual fun unpackFrontendFiles(baseDir: String, actualDir: String): Boolean {
    val path = NSBundle.mainBundle.pathForResource(name = "front", ofType = "zip") ?: return false
    val sourceURL = NSURL.fileURLWithPath(path)
    val targetPath = getNativePath("$baseDir/$actualDir")
    val destinationURL = NSURL.fileURLWithPath(targetPath.toString(), true)
    return SSZipArchive.unzipFileAtPath(path = sourceURL.path!!, toDestination = destinationURL.path!!)
}

@OptIn(ExperimentalForeignApi::class)
actual fun getNativePath(fileName: String): Path {
    val producePath = {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        requireNotNull(documentDirectory).path + "/$fileName"
    }
    return producePath().toPath()
}