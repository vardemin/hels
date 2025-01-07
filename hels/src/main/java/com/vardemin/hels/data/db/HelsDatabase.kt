package com.vardemin.hels.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vardemin.hels.data.db.converter.LocalDateTimeConverter
import com.vardemin.hels.data.db.converter.MapOfStringListConverter
import com.vardemin.hels.data.db.converter.MapOfStringsConverter
import com.vardemin.hels.data.db.dao.EventsDao
import com.vardemin.hels.data.db.dao.LogsDao
import com.vardemin.hels.data.db.dao.RequestsDao
import com.vardemin.hels.data.db.dao.SessionsDao
import com.vardemin.hels.data.db.entity.EventEntity
import com.vardemin.hels.data.db.entity.LiteRequestEntity
import com.vardemin.hels.data.db.entity.LogItemEntity
import com.vardemin.hels.data.db.entity.RequestEntity
import com.vardemin.hels.data.db.entity.SessionEntity

private const val DB_VERSION = 1

@Database(
    entities = [SessionEntity::class, LogItemEntity::class, RequestEntity::class, EventEntity::class],
    views = [LiteRequestEntity::class],
    version = DB_VERSION
)
@TypeConverters(
    LocalDateTimeConverter::class,
    MapOfStringsConverter::class,
    MapOfStringListConverter::class
)
internal abstract class HelsDatabase : RoomDatabase() {
    abstract fun logsDao(): LogsDao
    abstract fun requestsDao(): RequestsDao
    abstract fun sessionsDao(): SessionsDao
    abstract fun eventsDao(): EventsDao
}