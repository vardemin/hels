package com.vardemin.hels.data.db.converter

import androidx.room.TypeConverter

internal object MapOfStringsConverter {
    private const val SEPARATOR = "|;|"
    @TypeConverter
    fun toMap(mapString: String): Map<String, String> {
        val entries = mapString.split(SEPARATOR).mapNotNull { keyValue ->
            keyValue.split("=").takeIf { it.size > 1 }?.let {
                it[0] to it[1]
            }
        }
        return entries.toMap()
    }

    @TypeConverter
    fun toMapString(map: Map<String, String>): String {
        return map.entries.joinToString(SEPARATOR) {
            "${it.key}=${it.value}"
        }
    }
}