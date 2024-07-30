package com.vardemin.hels.migration

import com.vardemin.hels.data.KEY_FRONT_VERSION
import com.vardemin.hels.data.getPrefsDS
import com.vardemin.hels.utils.deleteRecursively
import com.vardemin.hels.utils.mkDir
import com.vardemin.hels.utils.unpackFrontendFiles
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object HelsMigrator {
    fun migrate(baseFrontDir: String, version: Int): String {
        val prefsDs = getPrefsDS()
        val previousVersion: Int = runBlocking { prefsDs.data.first() }[KEY_FRONT_VERSION] ?: -1

        return if (previousVersion != version) {
            deleteRecursively(baseFrontDir)
            val targetDir = "front_$version"
            if (mkDir(baseFrontDir, targetDir) && unpackFrontendFiles(baseFrontDir, targetDir)) {
                CoroutineScope(Dispatchers.IO).launch {
                    prefsDs.updateData { prefs ->
                        prefs.toMutablePreferences().apply { set(KEY_FRONT_VERSION, version) }
                    }
                }
            } else {
                Napier.d("Error occurred while migrating front", tag = "HelMigrator")
                throw IllegalStateException("Could not unzip front to target directory")
            }
            Napier.i("Migrated from $previousVersion to $version", tag = "HelMigrator")
            "$baseFrontDir/$targetDir"
        } else {
            Napier.i("No migration needed", tag = "HelMigrator")
            "$baseFrontDir/front_$version"
        }
    }
}