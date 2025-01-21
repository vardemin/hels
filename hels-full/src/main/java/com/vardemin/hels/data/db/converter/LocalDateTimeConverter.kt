package com.vardemin.hels.data.db.converter

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDateTime

internal object LocalDateTimeConverter {
    @TypeConverter
    fun toDate(dateString: String): LocalDateTime {
        return LocalDateTime.parse(dateString)
    }

    @TypeConverter
    fun toDateString(date: LocalDateTime): String {
        return date.toString()
    }
}