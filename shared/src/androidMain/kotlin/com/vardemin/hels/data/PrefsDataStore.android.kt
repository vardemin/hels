package com.vardemin.hels.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.vardemin.hels.ContextHolder

fun createDataStore(context: Context): DataStore<Preferences> = createPrefsDataStore(
    producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
)

actual fun getPrefsDS(): DataStore<Preferences> = createDataStore(ContextHolder.context)