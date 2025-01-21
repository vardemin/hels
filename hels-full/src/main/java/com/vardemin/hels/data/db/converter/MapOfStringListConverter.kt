package com.vardemin.hels.data.db.converter

import androidx.room.TypeConverter

internal object MapOfStringListConverter {
    private const val SEPARATOR = "|;|"
    private const val KEY_VALUE_SEPARATOR = "|=|"
    private const val VALUES_SEPARATOR = "|&|"

    @TypeConverter
    fun toMap(mapString: String): Map<String, List<String>> {
        val entries = mapString.split(SEPARATOR).mapNotNull { keyValue ->
            keyValue.split(KEY_VALUE_SEPARATOR).takeIf { it.size > 1 }?.let {
                val values = it[1].split(VALUES_SEPARATOR)
                it[0] to values
            }
        }
        return entries.toMap()
    }

    @TypeConverter
    fun toMapString(map: Map<String, List<String>>): String {
        return map.entries.joinToString(SEPARATOR) {
            val valuesString = it.value.joinToString(VALUES_SEPARATOR)
            "${it.key}${KEY_VALUE_SEPARATOR}${valuesString}"
        }
    }
}