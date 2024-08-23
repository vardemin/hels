package com.vardemin.hels.utils

import com.vardemin.hels.ContextHolder
import okio.Path
import okio.Path.Companion.toPath
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

actual fun deleteRecursively(directory: String) {
    ContextHolder.context.filesDir.resolve(directory).deleteRecursively()
}

actual fun mkDir(baseDir: String, actualDir: String): Boolean {
    return ContextHolder.context.filesDir.resolve("$baseDir/$actualDir").mkdirs()
}

actual fun unpackFrontendFiles(baseDir: String, actualDir: String): Boolean {
    try {
        var filename: String
        val inputStream = ContextHolder.context.assets.open("front.zip")
        val zis = ZipInputStream(BufferedInputStream(inputStream))
        var ze: ZipEntry
        val buffer = ByteArray(1024)
        var count: Int

        while (zis.nextEntry.also { ze = it } != null) {
            filename = ze.name
            val fileDir = ContextHolder.context.filesDir.resolve("$baseDir/$actualDir")
            val file = File(fileDir, filename)
            if (ze.isDirectory) {
                file.mkdirs()
                continue
            }
            if (file.parentFile?.exists() == false) {
                file.parentFile?.mkdirs()
            }
            if (!file.exists()) {
                file.createNewFile()
            }
            val fileOutputStream = FileOutputStream(file, false)
            while (zis.read(buffer).also { count = it } != -1) {
                fileOutputStream.write(buffer, 0, count)
            }
            fileOutputStream.close()
            zis.closeEntry()
        }

        zis.close()
    } catch (e: IOException) {
        e.printStackTrace()
        return false
    }catch (e:NullPointerException){
        e.printStackTrace()
        return false
    }
    return true
}

actual fun getNativePath(fileName: String): Path {
    val ctx = ContextHolder.context
   return ctx.filesDir.resolve(fileName).absolutePath.toPath()
}