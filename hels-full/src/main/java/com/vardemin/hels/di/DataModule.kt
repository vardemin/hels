package com.vardemin.hels.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.vardemin.hels.data.db.HelsDatabase
import com.vardemin.hels.model.mapper.HelsEventsMapper
import com.vardemin.hels.model.mapper.HelsLiteRequestsMapper
import com.vardemin.hels.model.mapper.HelsLogsMapper
import com.vardemin.hels.model.mapper.HelsRequestsMapper
import com.vardemin.hels.model.mapper.HelsSessionMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.coroutines.CoroutineContext

internal class DataModule(
    val context: Context,
    val port: Int,
    val frontDirectory: File
) {
    val database = Room.databaseBuilder(context, HelsDatabase::class.java, "hels")
        .fallbackToDestructiveMigration()
        .build()

    val logsDao get() = database.logsDao()
    val eventsDao get() = database.eventsDao()
    val requestsDao get() = database.requestsDao()
    val sessionsDao get() = database.sessionsDao()

    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("hels_prefs", Context.MODE_PRIVATE)

    val logsMapper get() = HelsLogsMapper()
    val requestsMapper get() = HelsRequestsMapper()
    val requestsLiteMapper get() = HelsLiteRequestsMapper()
    val sessionsMapper get() = HelsSessionMapper()
    val eventsMapper get() = HelsEventsMapper()

    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        useAlternativeNames = true
        encodeDefaults = true
    }

    val defaultCoroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Default
    val defaultScope: CoroutineScope = CoroutineScope(defaultCoroutineContext)
}