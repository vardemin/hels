package com.vardemin.hels.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import okio.Path.Companion.toPath

internal const val dataStoreFileName = "prefs.preferences_pb"
internal val KEY_FRONT_VERSION = intPreferencesKey("hels_front_version")

fun createPrefsDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )

expect fun getPrefsDS(): DataStore<Preferences>