package com.vardemin.hels.model

import kotlinx.serialization.json.Json

interface HelsItem {
    fun toJson(json: Json): String
}