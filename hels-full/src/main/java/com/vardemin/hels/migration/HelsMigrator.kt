package com.vardemin.hels.migration

import android.content.Context
import com.vardemin.hels.full.R
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

internal object HelsMigrator {

    private const val KEY_FRONT_VERSION = "hels_front_version"
    private const val KEY_CUSTOM_FRONT_VERSION = "hels_custom_front_version"

    fun migrate(context: Context, directory: File, version: Int, custom: Boolean): File {
        val sharedPreferences = context.getSharedPreferences("hels", Context.MODE_PRIVATE)
        val frontPrefsKey = if (custom) KEY_CUSTOM_FRONT_VERSION else KEY_FRONT_VERSION
        val previousVersion = sharedPreferences.getInt(frontPrefsKey, -1)
        return if (previousVersion != version) {
            directory.deleteRecursively()
            val targetDir = directory.resolve("front_$version")
            targetDir.mkdirs()
            if (!targetDir.exists()) {
                targetDir.mkdir()
            }
            unpackFrontendFiles(context, targetDir)
            sharedPreferences.edit().putInt(frontPrefsKey, version).apply()
            targetDir
        } else directory.resolve("front_$version")
    }

    private fun unpackFrontendFiles(context: Context, directory: File): Boolean {
        try {
            var filename: String
            // Here put the Zip file path
            val inputStream = context.resources.openRawResource(R.raw.front)
            val zis = ZipInputStream(BufferedInputStream(inputStream))
            var ze: ZipEntry
            val buffer = ByteArray(1024)
            var count: Int

            while (zis.nextEntry.also { ze = it } != null) {
                filename = ze.name

                val file = File(directory, filename)
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
}