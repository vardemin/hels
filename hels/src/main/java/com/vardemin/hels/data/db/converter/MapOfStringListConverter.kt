package com.vardemin.hels.data.db.converter

import androidx.room.TypeConverter

internal object MapOfStringListConverter {
    private const val SEPARATOR = "|;|"

    @TypeConverter
    fun toMap(mapString: String): Map<String, List<String>> {
        val entries = mapString.split(SEPARATOR).mapNotNull { keyValue ->
            keyValue.split("=").takeIf { it.size > 1 }?.let {
                val values = it[1].split(",","[","]")
                it[0] to values
            }
        }
        return entries.toMap()
    }

    @TypeConverter
    fun toMapString(map: Map<String, List<String>>): String {
        return map.entries.joinToString(SEPARATOR) {
            val valuesString = it.value.joinToString(",", prefix = "[", postfix = "]")
            "${it.key}=${valuesString}"
        }
    }
}